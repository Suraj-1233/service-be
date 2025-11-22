package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SettingsRepository extends MongoRepository<Setting, String> {
    Setting findByKey(String key);
}
