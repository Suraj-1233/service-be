package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder; // ✅ injected from SecurityConfig
    @Autowired private GoogleTokenVerifier googleTokenVerifier;


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


    // ----------------------------
    // GOOGLE LOGIN - FINAL VERSION
    // ----------------------------
    public Map<String, Object> loginWithGoogle(String idToken) {

        System.out.println("🔵 STEP 1 — Received ID Token:");
        System.out.println("      " + idToken);

        // 1️⃣ VERIFY TOKEN
        System.out.println("🔵 STEP 2 — Verifying Google ID Token...");
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken);

        if (payload == null) {
            System.out.println("❌ STEP 2 FAILED — Invalid Google ID Token");
            throw new UnauthorizedException("Invalid Google ID Token");
        }

        System.out.println("✅ STEP 2 SUCCESS — Google Token Verified!");

        // 2️⃣ Extract Payload Info
        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        System.out.println("🔵 STEP 3 — Extracted Google User Data:");
        System.out.println("      Email: " + email);
        System.out.println("      Google ID: " + googleId);
        System.out.println("      Name: " + name);
        System.out.println("      Picture: " + picture);

        User user;

        // 3️⃣ CHECK BY GOOGLE ID
        System.out.println("🔵 STEP 4 — Checking if GOOGLE ID exists in DB...");
        Optional<User> byGoogleId = userRepository.findByGoogleId(googleId);

        if (byGoogleId.isPresent()) {
            System.out.println("✅ STEP 4 — Existing GOOGLE User Found!");
            user = byGoogleId.get();
        } else {
            System.out.println("❌ STEP 4 — No user found with this Google ID.");

            // 4️⃣ CHECK BY EMAIL
            System.out.println("🔵 STEP 5 — Checking if EMAIL already exists in DB...");
            Optional<User> byEmail = userRepository.findByEmail(email);

            if (byEmail.isPresent()) {
                System.out.println("🔵 STEP 5 RESULT — Email found! Linking Google account to existing user.");
                user = byEmail.get();

                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                user.setName(name);
                user.setProfilePicture(picture);
                user.setUpdatedAt(LocalDateTime.now());

            } else {
                // 5️⃣ NEW GOOGLE SIGNUP
                System.out.println("🟢 STEP 6 — New Google User Signup!");

                user = new User();
                user.setEmail(email);
                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                user.setName(name);
                user.setProfilePicture(picture);
                user.setRole("CUSTOMER");
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
            }
        }

        // 6️⃣ SAVE USER
        System.out.println("🔵 STEP 7 — Saving user in DB...");
        userRepository.save(user);
        System.out.println("✅ STEP 7 DONE — User saved!");

        // 7️⃣ GENERATE JWT TOKEN
        System.out.println("🔵 STEP 8 — Generating JWT Token...");
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        System.out.println("🟢 STEP 8 SUCCESS — JWT Token Generated:");
        System.out.println("      " + token);

        // 8️⃣ FINAL RESPONSE
        System.out.println("🏁 STEP 9 — Returning Response → token");
        return Map.of("token", token);
    }

}
