package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.dto.AddressRequest;
import com.LaundryApplication.LaundryApplication.dto.AddressResponse;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.Address;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final UserRepository userRepository;

    public AddressController(AddressService addressService, UserRepository userRepository) {
        this.addressService = addressService;
        this.userRepository = userRepository;
    }

    // Get all addresses for authenticated user
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Address> addresses = addressService.getAddresses(user.getId());
        List<AddressResponse> resp = addresses.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    // Add new address
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address entity = toEntity(request);
        entity.setUserId(user.getId());

        Address saved = addressService.addAddress(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    // Update address
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable("id") String addressId,
                                                         @Valid @RequestBody AddressRequest request,
                                                         Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address existing = addressService.getAddresses(user.getId()).stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // copy fields from request
        existing.setFullName(request.getFullName());
        existing.setMobileNumber(request.getMobileNumber());
        existing.setFlatBuilding(request.getFlatBuilding());
        existing.setAreaStreet(request.getAreaStreet());
        existing.setCity(request.getCity());
        existing.setPincode(request.getPincode());
        existing.setLabel(request.getLabel());
        existing.setLatitude(request.getLatitude());
        existing.setLongitude(request.getLongitude());

        Address updated = addressService.updateAddress(existing);
        return ResponseEntity.ok(toResponse(updated));
    }

    // Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") String addressId, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address existing = addressService.getAddresses(user.getId()).stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressService.deleteAddress(existing.getId());
        return ResponseEntity.noContent().build();
    }

    // Mapping helpers
    private AddressResponse toResponse(Address a) {
        AddressResponse r = new AddressResponse();
        r.setId(a.getId());
        r.setUserId(a.getUserId());
        r.setFullName(a.getFullName());
        r.setMobileNumber(a.getMobileNumber());
        r.setFlatBuilding(a.getFlatBuilding());
        r.setAreaStreet(a.getAreaStreet());
        r.setCity(a.getCity());
        r.setPincode(a.getPincode());
        r.setLabel(a.getLabel());
        r.setLatitude(a.getLatitude());
        r.setLongitude(a.getLongitude());
        r.setDefault(a.isDefault());
        return r;
    }

    private Address toEntity(AddressRequest req) {
        Address a = new Address();
        a.setFullName(req.getFullName());
        a.setMobileNumber(req.getMobileNumber());
        a.setFlatBuilding(req.getFlatBuilding());
        a.setAreaStreet(req.getAreaStreet());
        a.setCity(req.getCity());
        a.setPincode(req.getPincode());
        a.setLabel(req.getLabel());
        a.setLatitude(req.getLatitude());
        a.setLongitude(req.getLongitude());
        return a;
    }
}
