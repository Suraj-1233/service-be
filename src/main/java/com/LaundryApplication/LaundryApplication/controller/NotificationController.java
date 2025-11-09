package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body) {

        return notificationService.sendNotification(title, body, token);
    }
}
