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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(
        name = "Authentication",
        description = "Register new users and log in to receive JWT tokens"
)
public class AuthController {

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
            throw new RuntimeException("Full name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        AppUser user = new AppUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser savedUser = appUserRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Registration successful",
                token
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        AppUser user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                "Login successful",
                token
        );
                
    }
}