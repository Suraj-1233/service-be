package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.Order;
import com.LaundryApplication.LaundryApplication.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId); // ✅ works only if field exists

    long countByStatus(String status); // e.g. PENDING, COMPLETED, etc.
    long countByStatus(OrderStatus status);   // ✅ use enum directly
    List<Order> findByStatus(OrderStatus status);
    List<Order> findTop20ByOrderByCreatedAtDesc();

    @Query("{ 'userId': ?0, 'status': { $nin: ['DELIVERED', 'COMPLETED', 'CANCELLED'] } }")
    List<Order> findTop5ActiveOrdersByUserId(String userId);
}
