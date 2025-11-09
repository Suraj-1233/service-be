package com.LaundryApplication.LaundryApplication.controller;


import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class NotificationTokenController {

    private final UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public NotificationTokenController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @PutMapping("/update")
    public ResponseEntity<?> updateFcmToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);
            System.out.println("✅  email in updates sent successfully: " + email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String fcmToken = body.get("fcmToken");
            user.setFcmToken(fcmToken);
            userRepository.save(user);

            return ResponseEntity.ok("FCM token updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update token: " + e.getMessage());
        }
    }




}
