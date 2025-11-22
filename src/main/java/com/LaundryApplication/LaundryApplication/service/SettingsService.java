package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.Setting;
import com.LaundryApplication.LaundryApplication.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    public String getSupportPhone() {
        Setting setting = settingsRepository.findByKey("supportPhone");
        return setting != null ? setting.getValue() : "";
    }

    public void updateSupportPhone(String phone) {
        Setting setting = settingsRepository.findByKey("supportPhone");
        if (setting == null) {
            setting = new Setting("supportPhone", phone);
        } else {
            setting.setValue(phone);
        }
        settingsRepository.save(setting);
    }
}
