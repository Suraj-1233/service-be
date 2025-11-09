package com.LaundryApplication.LaundryApplication.dto;

import lombok.Data;
import java.util.List;

@Data
public class ServiceRequest {
    private String serviceId;
    private String serviceName;
    private List<ItemRequest> items;
}
