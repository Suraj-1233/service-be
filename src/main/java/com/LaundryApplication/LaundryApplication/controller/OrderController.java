package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.dto.CreateOrderRequest;
import com.LaundryApplication.LaundryApplication.exception.*;
import com.LaundryApplication.LaundryApplication.model.*;
import com.LaundryApplication.LaundryApplication.repository.*;
import com.LaundryApplication.LaundryApplication.security.JwtUtil;
import com.LaundryApplication.LaundryApplication.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // ✅ Create a new order (for the logged-in user)
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

//        order.setUserId(user.getId());
        Order order = orderService.mapRequestToOrder(request, user.getId());

        log.info("✅ Order details in controller. orderId={}", order);

        Order savedOrder = orderService.createOrder(order, request.getPickupAddressId());
        notifyAdmins(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // ✅ 2️⃣ Get all orders (ADMIN only)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(Authentication auth) {
        ensureAdmin(auth);
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ✅ 3️⃣ Get all orders of current user
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return ResponseEntity.ok(orderService.getOrdersOfCurrentUser());
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String id,
                                                   @RequestParam String status,
                                                   Authentication auth) {
        ensureAdmin(auth);

        Order updatedOrder = orderService.updateOrderStatus(id, status);
        User customer = userRepository.findById(updatedOrder.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Notify user
        if (customer.getFcmToken() != null) {
            notificationService.sendNotification(
                    "🚚 Order Update",
                    "Your order #" + updatedOrder.getId() + " is now " + status,
                    customer.getFcmToken()
            );
        }

        return ResponseEntity.ok(updatedOrder);
    }


    // ✅ 6️⃣ Cancel order (user or admin)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable String id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") String id,  Authentication auth) {
        if (auth == null) throw new UnauthorizedException("Invalid authentication context");
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ADMIN"));
        if (!isAdmin && !order.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied to this order");
        }
        return ResponseEntity.ok(order);
    }

    // ✅ 7️⃣ Admin-only: Get orders by specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId, Authentication auth) {
        ensureAdmin(auth);
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }


    // ✅ 8️⃣ Update FCM Token
    @PutMapping("/update-fcm-token")
    public ResponseEntity<String> updateFcmToken(@RequestBody Map<String, String> body, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String fcmToken = body.get("fcmToken");
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            throw new BadRequestException("FCM token cannot be empty");
        }

        user.setFcmToken(fcmToken);
        userRepository.save(user);

        return ResponseEntity.ok("FCM token updated successfully");
    }


    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveOrders() {
        List<Order> orders = orderService.getRecentActiveOrders();

        List<Map<String, Object>> response = orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", o.getId());
            map.put("status", o.getStatus().name());
            map.put("pickupDate", o.getPickupDate() != null ? o.getPickupDate().toString() : "");
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }



    private void notifyAdmins(User user) {
        userRepository.findAllActiveAdmins().forEach(admin -> {
            if (admin.getFcmToken() != null) {
                notificationService.sendNotification(
                        "🧺 New Order Received",
                        "New order placed by " + user.getName(),
                        admin.getFcmToken()
                );
            }
        });
    }



    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) {
            throw new UnauthorizedException("No authentication found");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ADMIN"));
        if (!isAdmin) {
            throw new UnauthorizedException("Access denied: Admin only");
        }
    }

}
