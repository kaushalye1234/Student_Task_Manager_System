package com.chamindu.taskManager.controller;

import com.chamindu.taskManager.dto.AuthResponse;
import com.chamindu.taskManager.dto.LoginRequest;
import com.chamindu.taskManager.dto.RegisterRequest;
import com.chamindu.taskManager.model.AppUser;
import com.chamindu.taskManager.repository.AppUserRepository;
import com.chamindu.taskManager.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.chamindu.taskManager.exception.BadRequestException;
import com.chamindu.taskManager.exception.UnauthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Register new users and log in to receive JWT tokens")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            AppUserRepository appUserRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser savedUser = appUserRepository.save(user);

        log.info(
                "Registered new user with userId={}",
                savedUser.getId());

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Registration successful",
                token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login rejected: invalid credentials");

                    return new UnauthorizedException(
                            "Invalid email or password");
                });

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            log.warn("Login rejected: invalid credentials");

            throw new UnauthorizedException(
                    "Invalid email or password");
        }

        log.info(
                "User login successful for userId={}",
                user.getId());

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                "Login successful",
                token);

    }
}