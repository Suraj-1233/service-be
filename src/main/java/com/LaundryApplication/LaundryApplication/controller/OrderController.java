package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.Order;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.AddressRepository;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import com.LaundryApplication.LaundryApplication.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    // ✅ Create a new order (for the logged-in user)
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order, HttpServletRequest request) {
        try {

                // 🔹 Extract JWT token from header
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
                String token = authHeader.substring(7);

            String email = jwtUtil.getEmailFromToken(token); // returns "test.kannujiya@example.com"
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId(); // now you have the userId
            order.setUserId(userId);

            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ✅ Get all orders (admin only, or internal)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // ✅ Get all orders of currently logged-in user
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders() {
        try {
            List<Order> myOrders = orderService.getOrdersOfCurrentUser();
            return ResponseEntity.ok(myOrders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to fetch user orders: " + e.getMessage());
        }
    }

    // ✅ Update order status (admin or system)
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable("id") String id,
            @RequestParam("status") String status
    ) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedOrder);
    }

    // ✅ Cancel order (user or admin)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable("id") String id) {
        try {
            Order cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token); // returns "test.kannujiya@example.com"

            String role = jwtUtil.getRoleFromToken(token);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String userId = user.getId(); // now you have the userId

            // Fetch the order
            var orderOpt = orderService.getOrderById(id);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Order order = orderOpt.get();
            System.out.println(order);

            // Check if user is owner or admin
            if (!userId.equals(order.getUserId()) && !"ADMIN".equalsIgnoreCase(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") String userId) {  // ✅ Explicit name added
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body("Access denied");
        }

        List<Order> orders = orderRepository.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    private boolean isAdmin(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String role = jwtUtil.getRoleFromToken(token);
            return role.equals("ADMIN");
        }
        return false;
    }



}
