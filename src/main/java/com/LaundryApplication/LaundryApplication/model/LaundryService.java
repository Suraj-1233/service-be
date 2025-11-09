package com.LaundryApplication.LaundryApplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "services")
public class LaundryService {
    @Id
    private String id;
    private String serviceName;   // e.g., Wash, Iron, Dry Clean
    private String description;   // e.g., “Gentle wash and dry”
    private double basePrice;     // default price per item
    private boolean active = true; // for admin toggle
}
