package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.LaundryService;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LaundryServiceRepository extends MongoRepository<LaundryService, String> {
}
