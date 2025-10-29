package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.model.Address;
import com.LaundryApplication.LaundryApplication.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // ✅ Get all addresses for a specific user
    public List<Address> getAddresses(String userId) {
        return addressRepository.findByUserId(userId);
    }

    // ✅ Add a new address (mobile required)
    public Address addAddress(Address address) {
        System.out.println("Saving Address: " + address);

//        if (address.getPhoneNumber() == null || address.getPhoneNumber().trim().isEmpty()) {
//            throw new RuntimeException("Phone number is required for an address");
//        }
        if (address.getUserId() == null || address.getUserId().trim().isEmpty()) {
            throw new RuntimeException("User ID is required for an address");
        }
        return addressRepository.save(address);
    }

    // ✅ Update existing address
    public Address updateAddress(Address address) {
        if (address.getId() == null || !addressRepository.existsById(address.getId())) {
            throw new RuntimeException("Address not found");
        }
        if (address.getMobileNumber() == null || address.getMobileNumber().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required for an address");
        }
        return addressRepository.save(address);
    }

    // ✅ Delete address by ID
    public void deleteAddress(String addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(addressId);
    }

    // ✅ Get single address by ID
    public Optional<Address> getAddressById(String addressId) {
        return addressRepository.findById(addressId);
    }
}
