package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.dto.GoogleLoginRequest;
import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ForbiddenException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.service.AuthService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    // ✅ 1️⃣ Register user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String message = authService.register(user);
        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    // ✅ 2️⃣ Login user (CUSTOMER / USER)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // ✅ 3️⃣ Login admin (ADMIN only)
    @PostMapping("/loginAdmin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!authService.passwordMatches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new ForbiddenException("Access denied: Not an admin account");
        }

        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new BadRequestException("idToken is required");
        }

        return ResponseEntity.ok(authService.loginWithGoogle(request.getIdToken()));
    }


    // ✅ DTOs
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }

    @Data
    public static class ApiResponse {
        private final boolean success;
        private final String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
