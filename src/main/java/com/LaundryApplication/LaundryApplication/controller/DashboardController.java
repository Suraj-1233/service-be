package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.Order;
import com.LaundryApplication.LaundryApplication.model.OrderStatus;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        Map<String, Object> stats = new LinkedHashMap<>();

        // ✅ Count total orders
        stats.put("totalOrders", orderRepository.count());

        // ✅ Count per status dynamically (works for all enums)
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            statusCounts.put(status.name(), orderRepository.countByStatus(status));
        }
        stats.put("statusCounts", statusCounts);

        // ✅ Calculate total revenue from completed/delivered orders
        List<Order> revenueOrders = orderRepository.findByStatus(OrderStatus.COMPLETED);
        double totalRevenue = revenueOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
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
