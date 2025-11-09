package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;

@Data
public class PricingResponse {
    private String id;
    private String itemId;
    private String itemName;
    private String serviceId;
    private String serviceName;
    private double price;
    private boolean active;
}
