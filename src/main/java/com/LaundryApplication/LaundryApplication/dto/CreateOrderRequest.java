package com.LaundryApplication.LaundryApplication.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String pickupDate;
    private String pickupTime;
    private String pickupAddressId;
    private String deliveryAddressId;
    private String note;
    private List<ServiceRequest> services;
    private double totalAmount;
    private String status;
}
