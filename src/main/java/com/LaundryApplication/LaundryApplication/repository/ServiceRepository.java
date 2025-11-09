package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends MongoRepository<Service, String> {
}
