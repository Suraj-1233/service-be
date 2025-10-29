package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId); // ✅ works only if field exists

    long countByStatus(String status); // e.g. PENDING, COMPLETED, etc.

    long countByStatusIgnoreCase(String status);
    List<Order> findByStatusIgnoreCase(String status);
    List<Order> findTop20ByOrderByCreatedAtDesc();
}
