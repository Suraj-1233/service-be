package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

  @Autowired
  private OrderRepository orderRepository;

    // ✅ 1. Get all users (Admin only)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllNonAdminUsers() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> !"admin".equalsIgnoreCase(user.getRole()))
                .toList();

        List<Map<String, Object>> result = users.stream().map(user -> {
            long orderCount = orderRepository.findByUserId(user.getId()).size(); // ✅ count user's orders

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("orderCount", orderCount); // ✅ number of orders by this user
            userData.put("phoneNumber", user.getPhoneNumber()); // ✅ added phone number

            return userData;
        }).toList();

        return ResponseEntity.ok(result);
    }



    // Get current logged-in user info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // extracted from JWT
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, Authentication authentication) {
        String email = authentication.getName(); // current logged-in user’s email

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update only editable fields
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
