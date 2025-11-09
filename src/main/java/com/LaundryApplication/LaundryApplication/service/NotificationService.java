package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    public String sendNotification(String title, String body, String fcmToken) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Notification sent successfully: " + response);
            return response;

        } catch (FirebaseMessagingException e) {
            System.err.println("❌ FirebaseMessagingException: " + e.getMessagingErrorCode());
            System.err.println("💬 Error message: " + e.getMessage());

            if (e.getMessagingErrorCode() != null &&
                    e.getMessagingErrorCode().name().equals("UNREGISTERED")) {
                // Token is invalid or expired → remove it
            }

            return "❌ Error sending notification: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error sending notification: " + e.getMessage();
        }
    }

}
