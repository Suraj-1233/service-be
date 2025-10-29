package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.Order;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Get dashboard stats (ADMIN only)
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        long totalOrders = orderRepository.count();
        long pending = orderRepository.countByStatusIgnoreCase("PENDING");
        long completed = orderRepository.countByStatusIgnoreCase("COMPLETED");

        // ✅ Calculate total revenue from completed orders
        List<Order> completedOrders = orderRepository.findByStatusIgnoreCase("COMPLETED");
        double totalRevenue = completedOrders.stream()
                .mapToDouble(o -> o.getTotalAmount())  // 🔥 Corrected field name
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("pending", pending);
        stats.put("completed", completed);
        stats.put("revenue", totalRevenue);

        return ResponseEntity.ok(stats);
    }

    // ✅ Get recent orders (ADMIN only)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentOrders(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<Order> recent = orderRepository.findTop20ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(recent);
    }

    // ✅ Helper to check if token belongs to ADMIN
    private boolean isAdmin(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return "ADMIN".equalsIgnoreCase(role);
        }
        return false;
    }
}
