package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.Service;
import com.LaundryApplication.LaundryApplication.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    // ✅ Get all services
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // ✅ Get service by ID
    public Service getServiceById(String id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
    }

    // ✅ Add new service
    public Service addService(Service service) {
        if (service.getName() == null || service.getName().isBlank()) {
            throw new BadRequestException("Service name is required");
        }
        return serviceRepository.save(service);
    }

    // ✅ Update existing service
    public Service updateService(String id, Service updatedService) {
        Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));

        existing.setName(updatedService.getName());
        existing.setDescription(updatedService.getDescription());
        existing.setActive(updatedService.isActive());
        existing.setIconUrl(updatedService.getIconUrl());

        return serviceRepository.save(existing);
    }

    // ✅ Delete service
    public void deleteService(String id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found with ID: " + id);
        }
        serviceRepository.deleteById(id);
    }
}
