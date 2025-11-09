package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.exception.UnauthorizedException;
import com.LaundryApplication.LaundryApplication.model.Service;
import com.LaundryApplication.LaundryApplication.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laundry-services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    // ✅ Public - Get all active services
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    // ✅ Public - Get service by ID
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable String id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    // ✅ Admin only - Add new service
    @PostMapping
    public ResponseEntity<Service> addService(@RequestBody Service service, Authentication auth) {
        ensureAdmin(auth);
        return ResponseEntity.ok(serviceService.addService(service));
    }

    // ✅ Admin only - Update existing service
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable String id,
                                                 @RequestBody Service updatedService,
                                                 Authentication auth) {
        ensureAdmin(auth);
        return ResponseEntity.ok(serviceService.updateService(id, updatedService));
    }

    // ✅ Admin only - Delete service
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable String id, Authentication auth) {
        ensureAdmin(auth);
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    // 🔒 Helper to enforce Admin role
    private void ensureAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new UnauthorizedException("Access denied: Admins only");
        }
    }
}
