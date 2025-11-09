package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.Order;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final String ownerNumber;

    public WhatsAppService(
            @Value("${twilio.accountSid}") String accountSid,
            @Value("${twilio.authToken}") String authToken,
            @Value("${twilio.fromNumber}") String fromNumber,
            @Value("${twilio.ownerNumber}") String ownerNumber
    ) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.ownerNumber = ownerNumber;

        Twilio.init(accountSid, authToken);
    }

    public void sendOrderNotification(Order order) {
        String pickupDateStr = order.getPickupDate() != null ? order.getPickupDate().toString() : "Not provided";
        String deliveryDateStr = order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : "Not provided";

        String messageBody = String.format(
                "New Order Received!\n\n" +
                        "Order ID: %s\n" +
                        "Customer: %s\n" +

                        "Items: %s\n" +
                        "Total: ₹%.2f\n" +
                        "Pickup Date: %s\n" +
                        "Delivery Date: %s",
                order.getId(),
                order.getCustomerName(),
                order.getTotalAmount(),
                pickupDateStr,
                deliveryDateStr
        );

        Message message = Message.creator(
                new PhoneNumber(ownerNumber),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();

        System.out.println("WhatsApp message sent. SID: " + message.getSid());
    }


    public void sendOrderCancelledNotification(Order order) {



        String messageBody = String.format(
                "Order Cancelled!\n\nOrder ID: %s\nCustomer: %s\nPhone: %s\nItems: %s\nTotal: ₹%.2f\nLocation: %s, %s\nStatus: %s",
                order.getId(),
                order.getCustomerName(),
                order.getTotalAmount(),
                order.getStatus()
        );


        Message message = Message.creator(
                    new PhoneNumber(ownerNumber),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();

            System.out.println("WhatsApp notification sent: " + message.getSid());
        }
    }
