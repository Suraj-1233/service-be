package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder; // ✅ injected from SecurityConfig

    // ✅ 1️⃣ Register a new user
    public String register(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider("LOCAL");

        // Default role if not set
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("CUSTOMER");
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    // ✅ 2️⃣ Login (returns JWT token)
    public String login(String email, String password) {
        if (email == null || password == null) {
            throw new BadRequestException("Email and password are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }



        return jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
    }

    // ✅ 3️⃣ Get user by email (used for admin login)
    public User getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ✅ 4️⃣ Password comparison (utility)
    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
