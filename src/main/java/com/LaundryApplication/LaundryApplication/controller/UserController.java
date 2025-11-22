package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired private UserService userService;

    // ✅ 1️⃣ Get all users (Admin only)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(Authentication auth) {
        ensureAdmin(auth);
        return ResponseEntity.ok(userService.getAllNonAdminUsers());
    }

    // ✅ 2️⃣ Get current user profile
    // ✅ 2️⃣ Get current user profile
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(userService.getCurrentUser(email));
    }


    // ✅ 3️⃣ Update logged-in user profile
    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser, Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(userService.updateUser(email, updatedUser));
    }

    // 🔒 Helper — restricts Admin-only access
    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new UnauthorizedException("Access denied: Admins only");
        }
    }
}
