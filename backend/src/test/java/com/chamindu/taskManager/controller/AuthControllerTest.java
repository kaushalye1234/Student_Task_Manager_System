package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.dto.AuthResponse;
import com.chamindu.taskManager.dto.LoginRequest;
import com.chamindu.taskManager.dto.RegisterRequest;
import com.chamindu.taskManager.exception.BadRequestException;
import com.chamindu.taskManager.exception.UnauthorizedException;
import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.security.JwtUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerReturnsAuthResponseWhenRequestIsValid() {

        RegisterRequest request = new RegisterRequest(
                "Test Student",
                "student@example.com",
                "secret123"
        );

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setFullName("Test Student");
        savedUser.setEmail("student@example.com");
        savedUser.setPassword("encoded-password");

        when(appUserRepository.existsByEmail(
                "student@example.com"
        )).thenReturn(false);

        when(passwordEncoder.encode("secret123"))
                .thenReturn("encoded-password");

        when(appUserRepository.save(any(AppUser.class)))
                .thenReturn(savedUser);

        when(jwtUtil.generateToken("student@example.com"))
                .thenReturn("registration-token");

        AuthResponse response =
                authController.register(request);

        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals(
                "Test Student",
                response.getFullName()
        );
        assertEquals(
                "student@example.com",
                response.getEmail()
        );
        assertEquals(
                "Registration successful",
                response.getMessage()
        );
        assertEquals(
                "registration-token",
                response.getToken()
        );

        ArgumentCaptor<AppUser> userCaptor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(appUserRepository)
                .save(userCaptor.capture());

        AppUser userPassedToRepository =
                userCaptor.getValue();

        assertEquals(
                "encoded-password",
                userPassedToRepository.getPassword()
        );
    }

    @Test
    void registerRejectsDuplicateEmail() {

        RegisterRequest request = new RegisterRequest(
                "Test Student",
                "student@example.com",
                "secret123"
        );

        when(appUserRepository.existsByEmail(
                "student@example.com"
        )).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authController.register(request)
        );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(appUserRepository, never())
                .save(any(AppUser.class));

        verifyNoInteractions(jwtUtil);
    }

    @Test
    void loginReturnsAuthResponseWhenCredentialsAreCorrect() {

        LoginRequest request = new LoginRequest(
                "student@example.com",
                "secret123"
        );

        AppUser storedUser = new AppUser();
        storedUser.setId(1L);
        storedUser.setFullName("Test Student");
        storedUser.setEmail("student@example.com");
        storedUser.setPassword("encoded-password");

        when(appUserRepository.findByEmail(
                "student@example.com"
        )).thenReturn(Optional.of(storedUser));

        when(passwordEncoder.matches(
                "secret123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtUtil.generateToken("student@example.com"))
                .thenReturn("login-token");

        AuthResponse response =
                authController.login(request);

        assertEquals(Long.valueOf(1L), response.getId());
        assertEquals(
                "student@example.com",
                response.getEmail()
        );
        assertEquals(
                "Login successful",
                response.getMessage()
        );
        assertEquals(
                "login-token",
                response.getToken()
        );
    }

    @Test
    void loginRejectsWrongPassword() {

        LoginRequest request = new LoginRequest(
                "student@example.com",
                "wrong-password"
        );

        AppUser storedUser = new AppUser();
        storedUser.setId(1L);
        storedUser.setEmail("student@example.com");
        storedUser.setPassword("encoded-password");

        when(appUserRepository.findByEmail(
                "student@example.com"
        )).thenReturn(Optional.of(storedUser));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authController.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(jwtUtil, never())
                .generateToken(anyString());
    }

    @Test
    void loginRejectsUnknownEmail() {

        LoginRequest request = new LoginRequest(
                "unknown@example.com",
                "secret123"
        );

        when(appUserRepository.findByEmail(
                "unknown@example.com"
        )).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authController.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verifyNoInteractions(
                passwordEncoder,
                jwtUtil
        );
    }
}