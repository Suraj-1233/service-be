package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    // 🔹 GET Support Phone Number
    @GetMapping("/support/phone")
    public String getSupportPhone() {
        return settingsService.getSupportPhone();
    }

    // 🔹 UPDATE Support Phone Number (Optional Admin API)
    @PostMapping("/support/phone")
    public String updateSupportPhone(@RequestBody String phone) {
        settingsService.updateSupportPhone(phone);
        return "Support phone updated!";
    }
}
