package com.LaundryApplication.LaundryApplication.repository;


import com.LaundryApplication.LaundryApplication.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
}

