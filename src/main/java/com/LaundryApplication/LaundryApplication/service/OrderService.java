package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.dto.CreateOrderRequest;
import com.LaundryApplication.LaundryApplication.dto.ItemRequest;
import com.LaundryApplication.LaundryApplication.dto.ServiceRequest;
import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.*;
import com.LaundryApplication.LaundryApplication.repository.AddressRepository;
import com.LaundryApplication.LaundryApplication.repository.OrderRepository;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService  {

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private AddressRepository addressRepository;
    @Autowired private PricingService pricingService;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // ✅ Create new order
    public Order createOrder(Order order, String addressId) {
        log.info("🧾 [ORDER CREATION INITIATED]");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        log.debug("🔐 Authenticated user email: {}", userEmail);

        // 1️⃣ Validate user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        order.setUserId(user.getId());
        log.info("✅ addressId saved before submit. orderId={}", addressId);

        // 2️⃣ Validate and capture address snapshot
        if (addressId != null && !addressId.isEmpty()) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new BadRequestException("Invalid address ID"));

            Order.AddressSnapshot snapshot = new Order.AddressSnapshot();
            snapshot.setLabel(address.getLabel());
            snapshot.setCity(address.getCity());
            snapshot.setPincode(address.getPincode());
            snapshot.setFlatBuilding(address.getFlatBuilding());
            snapshot.setFullName(address.getFullName());
            snapshot.setMobileNumber(address.getMobileNumber());
            snapshot.setAreaStreet(address.getAreaStreet());
            log.info("✅ snapshot saved before submit. orderId={}", snapshot);

            order.setAddressSnapshot(snapshot);
        }

        order.setTotalAmount(order.getTotalAmount());

        // 4️⃣ Initialize order meta
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        log.info("✅ Order saved before submit. orderId={}", order);

        // 5️⃣ Save order
        Order savedOrder = orderRepository.save(order);
        log.info("✅ Order saved successfully. orderId={}", savedOrder.getId());

        // 6️⃣ Send Notifications (Optional)
        try {
//            notificationService.sendOrderPlacedNotification(user, savedOrder);
        } catch (Exception e) {
        }

        return savedOrder;
    }

    // ✅ Get all orders (Admin)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ Get specific order
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    // ✅ Get all orders of current user
    public List<Order> getOrdersOfCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }

    // ✅ Update order status
    public Order updateOrderStatus(String id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setPreviousStatus(order.getStatus());
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
    }

    // ✅ Cancel order
    public Order cancelOrder(String orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Completed orders cannot be cancelled");
        }

        order.setPreviousStatus(order.getStatus());
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);

    }

    public List<Order> getRecentActiveOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Order> orders = orderRepository.findTop5ActiveOrdersByUserId(user.getId());

        // Sort by createdAt DESC (latest first)
        return orders.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .toList();
    }


    public Order mapRequestToOrder(CreateOrderRequest req, String userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setPickupDate(LocalDate.parse(req.getPickupDate()));
        order.setNote(req.getNote());
        order.setStatus(OrderStatus.valueOf(req.getStatus().toUpperCase()));
        order.setTotalAmount(req.getTotalAmount());
        order.setAddressId(req.getPickupAddressId());

        List<Order.OrderItem> items = new ArrayList<>();

        for (ServiceRequest serviceReq : req.getServices()) {
            for (ItemRequest itemReq : serviceReq.getItems()) {
                Order.OrderItem item = new Order.OrderItem();
                item.setItemId(itemReq.getId());
                item.setName(itemReq.getName());
                item.setQuantity(itemReq.getQuantity());

                Order.ServiceDetail service = new Order.ServiceDetail();
                service.setServiceId(serviceReq.getServiceId());
                service.setServiceName(serviceReq.getServiceName());
                service.setPrice(itemReq.getPrice());

                item.setServices(List.of(service));
                items.add(item);
            }
        }

        order.setItems(items);
        return order;
    }

}
