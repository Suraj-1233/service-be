package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "items")
public class Item {

    @Id
    private String id;

    private String name;           // e.g., "Shirt", "Trousers", "Bedsheet"
    private String category;       // e.g., "Clothing", "Household", etc.
    private String imageUrl;       // optional: icon or photo
    private boolean active = true; // for admin enable/disable

    private String description;    // optional: small detail about fabric type etc.
}
