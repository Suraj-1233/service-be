package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.Item;
import com.LaundryApplication.LaundryApplication.model.Pricing;
import com.LaundryApplication.LaundryApplication.model.PricingResponse;
import com.LaundryApplication.LaundryApplication.model.ServiceWithItemsResponse;
import com.LaundryApplication.LaundryApplication.repository.ItemRepository;
import com.LaundryApplication.LaundryApplication.repository.PricingRepository;
import com.LaundryApplication.LaundryApplication.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PricingService {

    @Autowired private PricingRepository pricingRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private ServiceRepository serviceRepository;

    // ✅ 1️⃣ Add or Update Pricing
    public Pricing addOrUpdatePricing(Pricing pricing) {
        if (pricing.getItemId() == null || pricing.getServiceId() == null) {
            throw new BadRequestException("Item ID and Service ID are required");
        }
        if (pricing.getPrice() <= 0) {
            throw new BadRequestException("Price must be greater than 0");
        }
        return pricingRepository.save(pricing);
    }

    // ✅ 2️⃣ Get Price for Item-Service Pair
    public double getPrice(String itemId, String serviceId) {
        return pricingRepository.findByItemIdAndServiceId(itemId, serviceId)
                .map(Pricing::getPrice)
                .orElseThrow(() -> new ResourceNotFoundException("No price found for item-service pair"));
    }

    // ✅ 3️⃣ Delete Pricing
    public void deletePricing(String id) {
        if (!pricingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pricing not found for ID: " + id);
        }
        pricingRepository.deleteById(id);
    }

    // ✅ 4️⃣ Get All Pricing with Item & Service Details
    public List<PricingResponse> getAllPricingDetails() {
        return pricingRepository.findAll().stream().map(pricing -> {
            var item = itemRepository.findById(pricing.getItemId()).orElse(null);
            var service = serviceRepository.findById(pricing.getServiceId()).orElse(null);

            PricingResponse dto = new PricingResponse();
            dto.setId(pricing.getId());
            dto.setItemId(pricing.getItemId());
            dto.setItemName(item != null ? item.getName() : null);
            dto.setServiceId(pricing.getServiceId());
            dto.setServiceName(service != null ? service.getName() : null);
            dto.setPrice(pricing.getPrice());
            dto.setActive(pricing.isActive());
            return dto;
        }).collect(Collectors.toList());
    }

    // ✅ 5️⃣ Get Services with Items & Prices
    public List<ServiceWithItemsResponse> getServicesWithItems() {
        var services = serviceRepository.findAll();
        var pricingList = pricingRepository.findAll();
        var items = itemRepository.findAll();

        Map<String, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, i -> i));

        Map<String, List<Pricing>> pricingByService = pricingList.stream()
                .collect(Collectors.groupingBy(Pricing::getServiceId));

        List<ServiceWithItemsResponse> responseList = new ArrayList<>();

        for (com.LaundryApplication.LaundryApplication.model.Service service : services) {
            ServiceWithItemsResponse res = new ServiceWithItemsResponse();
            res.setServiceId(service.getId());
            res.setServiceName(service.getName());

            List<ServiceWithItemsResponse.ItemWithPrice> itemList = pricingByService
                    .getOrDefault(service.getId(), new ArrayList<>())
                    .stream()
                    .map(p -> {
                        Item i = itemMap.get(p.getItemId());
                        ServiceWithItemsResponse.ItemWithPrice dto = new ServiceWithItemsResponse.ItemWithPrice();
                        if (i != null) {
                            dto.setItemId(i.getId());
                            dto.setItemName(i.getName());
                            dto.setPrice(p.getPrice());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            res.setItems(itemList);
            responseList.add(res);
        }

        return responseList;
    }
}
