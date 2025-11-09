package com.LaundryApplication.LaundryApplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;

    private AddressSnapshot addressSnapshot;
    @JsonProperty("services")
    private List<OrderItem> items;

    private LocalDate pickupDate;
    private LocalDate deliveryDate;
    private String customerName;
    private OrderStatus status;    // PLACED | IN_PROGRESS | READY | DELIVERED | CANCELLED
    private double totalAmount;
    private String note;
    @JsonProperty("pickupAddressId")
    private String addressId;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private OrderStatus previousStatus;
    // inner class for address snapshot
    @Data
    public static class AddressSnapshot {

        private String fullName;        // 🆕 matches Flutter model
        private String mobileNumber;    // required field
        private String flatBuilding;    // 🆕 house/flat info
        private String areaStreet;      // 🆕 area or street
        private String city;
        private String pincode;
        private String label;           // Home / Work / Other
        private Double latitude;        // 🆕 optional
        private Double longitude;       // 🆕 optional
        private boolean isDefault;
    }

    @Data
    public static class OrderItem {
        private String itemId;              // e.g., "I001"
        private String name;                // e.g., "Shirt"
        private int quantity;               // e.g., 2
        private List<ServiceDetail> services;  // Each item can have multiple services (Wash, Dry Clean)
    }

    // 🧩 Service details (Wash, Dry Clean, Iron, etc.)
    @Data
    public static class ServiceDetail {
        private String serviceId;           // e.g., "S003"
        private String serviceName;         // e.g., "Dry Clean"
        private double price;               // e.g., 100.0
    }





}
