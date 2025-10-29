package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.Address;
import com.LaundryApplication.LaundryApplication.model.Order;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.AddressRepository;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.utils.BaseLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends BaseLogger {

    private final UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private WhatsAppService whatsAppService;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    public OrderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Create new order
    public Order createOrder(Order order) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        logger.info("Creating new order for user: {}", order);

        logger.info("Creating new order for user: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUserId(user.getId());
        order.setCustomerName(user.getName());
        order.setStatus("PENDING");

        // ✅ Address linking and validation
        if (order.getAddressId() != null && !order.getAddressId().isEmpty()) {
            Address addr = addressRepository.findById(order.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Invalid address ID"));
            logger.info("Linked address {} to order {}", addr.getId(), order.getId());
        } else {
            // fallback — use user’s default info
            logger.info("Using user default address for order {}", order.getId());
        }

        // ✅ Save order
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully: {}", savedOrder.getId());

        // ✅ Send notifications
        try {
            notificationService.notifyOwnerNewOrder(savedOrder);
            whatsAppService.sendOrderNotification(savedOrder);
        } catch (Exception e) {
            logger.error("Failed to send notification for order {}", savedOrder.getId(), e);
        }

        return savedOrder;
    }

    // ✅ Get all orders (for admin)
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders...");
        return orderRepository.findAll();
    }

    // ✅ Get specific order
    public Optional<Order> getOrderById(String id) {
        logger.info("Fetching order by ID: {}", id);
        return orderRepository.findById(id);
    }

    // ✅ Update order status
    public Order updateOrderStatus(String id, String status) {
        logger.info("Updating order {} status to {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // ✅ Cancel order
    public Order cancelOrder(String orderId) {
        logger.info("Cancelling order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Completed orders cannot be cancelled");
        }

        order.setStatus("CANCELLED");
        whatsAppService.sendOrderCancelledNotification(order);
        return orderRepository.save(order);
    }

    // ✅ Get all orders of current user
    public List<Order> getOrdersOfCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        logger.info("Fetching orders for user {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(user.getId());
    }
}
