package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByFcmToken(String fcmToken);

    // ✅ Find all Admins (role == "ADMIN")
    @Query("{ 'role': 'ADMIN', 'fcmToken': { $ne: null } }")
    List<User> findAllActiveAdmins();

    // ✅ Find all Customers (role missing or not ADMIN)
    @Query("{ $or: [ { 'role': { $exists: false } }, { 'role': { $ne: 'ADMIN' } } ], 'fcmToken': { $ne: null } }")
    List<User> findAllActiveCustomers();

    Optional<User> findByGoogleId(String googleId);

}
