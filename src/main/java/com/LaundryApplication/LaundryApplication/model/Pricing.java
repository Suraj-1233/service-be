package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pricing")
public class Pricing {

    @Id
    private String id;

    private String itemId;       // ref → items._id
    private String serviceId;    // ref → services._id
    private double price;        // e.g., ₹50.0

    private boolean active = true;
}
