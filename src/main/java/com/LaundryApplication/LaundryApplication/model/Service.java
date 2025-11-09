package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "services")
public class Service {

    @Id
    private String id;
    private String name;         // e.g., "Wash & Fold"
    private String description;  // short details
    private boolean active = true;

    private String iconUrl;      // optional: for UI icon
}
