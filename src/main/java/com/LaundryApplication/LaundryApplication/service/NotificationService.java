package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.Order;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyOwnerNewOrder(Order order) {
        // For now, just log it
        System.out.println("WhatsApp Notification to Owner: New order received!");


    }
}
