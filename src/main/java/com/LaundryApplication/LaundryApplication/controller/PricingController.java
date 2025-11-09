package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.Pricing;
import com.LaundryApplication.LaundryApplication.model.PricingResponse;
import com.LaundryApplication.LaundryApplication.model.ServiceWithItemsResponse;
import com.LaundryApplication.LaundryApplication.service.PricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    @Autowired private PricingService pricingService;

    // ✅ 1️⃣ Add Pricing (Admin only)
    @PostMapping("/add")
    public ResponseEntity<?> addPricing(@RequestBody Pricing pricing, Authentication auth) {
        ensureAdmin(auth);
        Pricing saved = pricingService.addOrUpdatePricing(pricing);
        return ResponseEntity.ok(Map.of("message", "Pricing added successfully", "data", saved));
    }

    // ✅ 2️⃣ Update Pricing (Admin only)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePricing(@PathVariable String id, @RequestBody Pricing updated, Authentication auth) {
        ensureAdmin(auth);
        updated.setId(id);
        Pricing saved = pricingService.addOrUpdatePricing(updated);
        return ResponseEntity.ok(Map.of("message", "Pricing updated successfully", "data", saved));
    }

    // ✅ 3️⃣ Delete Pricing (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePricing(@PathVariable String id, Authentication auth) {
        ensureAdmin(auth);
        pricingService.deletePricing(id);
        return ResponseEntity.ok(Map.of("message", "Pricing deleted successfully"));
    }

    // ✅ 4️⃣ Get All Pricing (Admin or internal use)
    @GetMapping("/all")
    public ResponseEntity<List<PricingResponse>> getAllPricingWithDetails() {
        return ResponseEntity.ok(pricingService.getAllPricingDetails());
    }

    // ✅ 5️⃣ Get Price by Item & Service
    @GetMapping("/price")
    public ResponseEntity<Double> getPrice(@RequestParam String itemId, @RequestParam String serviceId) {
        double price = pricingService.getPrice(itemId, serviceId);
        return ResponseEntity.ok(price);
    }

    // ✅ 6️⃣ Get Services + Items + Prices (for user app)
    @GetMapping("/services-with-items")
    public ResponseEntity<List<ServiceWithItemsResponse>> getServicesWithItems() {
        return ResponseEntity.ok(pricingService.getServicesWithItems());
    }

    // 🔒 Helper
    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new UnauthorizedException("Access denied: Admins only");
        }
    }
}
