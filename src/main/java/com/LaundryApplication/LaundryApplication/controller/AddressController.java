package com.LaundryApplication.LaundryApplication.controller;

import com.LaundryApplication.LaundryApplication.model.Address;
import com.LaundryApplication.LaundryApplication.model.User;
import com.LaundryApplication.LaundryApplication.repository.UserRepository;
import com.LaundryApplication.LaundryApplication.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get all addresses for the authenticated user
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(Authentication auth) {
        String email = auth.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        List<Address> addresses = addressService.getAddresses(userOpt.get().getId());
        return ResponseEntity.ok(addresses);
    }

    // ✅ Add new address for authenticated user
    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUserId(user.getId());

        // Optional: Validate important fields
        if (address.getFullName() == null || address.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Full name is required for an address");
        }
        if (address.getMobileNumber() == null || address.getMobileNumber().trim().isEmpty()) {
            throw new RuntimeException("Mobile number is required for an address");
        }
        if (address.getFlatBuilding() == null || address.getFlatBuilding().trim().isEmpty()) {
            throw new RuntimeException("Flat/building information is required");
        }

        Address savedAddress = addressService.addAddress(address);
        return ResponseEntity.ok(savedAddress);
    }

    // ✅ Update existing address by ID
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable("id") String addressId,
                                                 @RequestBody Address newAddress,
                                                 Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure address belongs to this user
        Address existingAddress = addressService.getAddresses(user.getId()).stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // ✅ Update all address fields
        existingAddress.setFullName(newAddress.getFullName());
        existingAddress.setMobileNumber(newAddress.getMobileNumber());
        existingAddress.setFlatBuilding(newAddress.getFlatBuilding());
        existingAddress.setAreaStreet(newAddress.getAreaStreet());
        existingAddress.setCity(newAddress.getCity());
        existingAddress.setPincode(newAddress.getPincode());
        existingAddress.setLabel(newAddress.getLabel());
        existingAddress.setLatitude(newAddress.getLatitude());
        existingAddress.setLongitude(newAddress.getLongitude());
        existingAddress.setDefault(newAddress.isDefault());

        Address updatedAddress = addressService.updateAddress(existingAddress);
        return ResponseEntity.ok(updatedAddress);
    }

    // ✅ Delete address by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") String addressId,
                                              Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address existingAddress = addressService.getAddresses(user.getId()).stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressService.deleteAddress(existingAddress.getId());
        return ResponseEntity.noContent().build();
    }
}
