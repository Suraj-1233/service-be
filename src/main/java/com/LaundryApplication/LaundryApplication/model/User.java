package com.LaundryApplication.LaundryApplication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String addressId;

    // For email-password users
    private String password;

    // For Google Auth users
    private String googleId;
    private String provider; // "LOCAL" or "GOOGLE"

    private String mobile;
    private String role; // CUSTOMER / ADMIN
    private String fcmToken;
    private boolean isActive = true;
    private List<String> addresses;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String profilePicture;

}


