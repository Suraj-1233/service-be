package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userId;          // reference to User
    private String addressId;       // reference to Address
    private String customerName;
    private List<Item> items;
    private LocalDate pickupDate;
    private LocalDate deliveryDate;
    private double totalAmount;
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getItemsDescription() {
        if (items == null || items.isEmpty()) return "No items";
        return items.stream()
                .map(item -> item.getName() + " x" + item.getPrice())
                .collect(Collectors.joining(", "));
    }
}
