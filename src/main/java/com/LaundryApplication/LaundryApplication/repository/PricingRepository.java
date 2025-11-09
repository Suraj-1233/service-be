package com.LaundryApplication.LaundryApplication.repository;

import com.LaundryApplication.LaundryApplication.model.Pricing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRepository extends MongoRepository<Pricing, String> {
    Optional<Pricing> findByItemIdAndServiceId(String itemId, String serviceId);

    // ✅ New helper methods (for grouped queries)
    List<Pricing> findByServiceId(String serviceId);
    List<Pricing> findByItemId(String itemId);
}
