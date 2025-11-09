package com.LaundryApplication.LaundryApplication.model;

public enum OrderStatus {
    PLACED,             // when user places an order
    PICKUP_ASSIGNED,    // rider assigned for pickup
    PICKED_UP,          // order collected by rider
    IN_PROGRESS,        // washing/drying/folding
    READY,              // ready for delivery
    OUT_FOR_DELIVERY,   // rider assigned for delivery
    DELIVERED,          // completed and delivered
    CANCELLED ,          // order cancelled
    COMPLETED
}
