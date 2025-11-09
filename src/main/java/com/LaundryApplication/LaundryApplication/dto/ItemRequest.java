package com.LaundryApplication.LaundryApplication.dto;

import lombok.Data;

@Data
public class ItemRequest {
    private String id;
    private String name;
    private int quantity;
    private double price;
}
