package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;

    // ✅ 1️⃣ Get all non-admin users with order count
    public List<Map<String, Object>> getAllNonAdminUsers() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        return users.stream().map(user -> {
            long orderCount = orderRepository.findByUserId(user.getId()).size();

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("mobile", user.getMobile());
            userData.put("orderCount", orderCount);
            userData.put("active", user.isActive());
            return userData;
        }).toList();
    }

    // ✅ 2️⃣ Get current logged-in user
       public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    }


    // ✅ 3️⃣ Update user profile (self)
    public User updateUser(String email, User updatedUser) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(updatedUser.getName());
        user.setMobile(updatedUser.getMobile());
        user.setUpdatedAt(java.time.LocalDateTime.now());

        return userRepository.save(user);
    }
}
