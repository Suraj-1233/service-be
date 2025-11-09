package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import java.util.List;

@Data
public class ServiceWithItemsResponse {
    private String serviceId;
    private String serviceName;
    private List<ItemWithPrice> items;

    @Data
    public static class ItemWithPrice {
        private String itemId;
        private String itemName;
        private double price;
    }
}
