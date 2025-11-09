package com.LaundryApplication.LaundryApplication.service;

import com.LaundryApplication.LaundryApplication.exception.BadRequestException;
import com.LaundryApplication.LaundryApplication.exception.ResourceNotFoundException;
import com.LaundryApplication.LaundryApplication.model.Address;
import com.LaundryApplication.LaundryApplication.repository.AddressRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAddresses(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BadRequestException("User id is required");
        }
        return addressRepository.findByUserId(userId);
    }

    public Address addAddress(Address address) {
        if (address.getUserId() == null || address.getUserId().trim().isEmpty()) {
            throw new BadRequestException("User ID is required for an address");
        }
        // extra business checks can be here (e.g., max addresses per user)
        try {
            return addressRepository.save(address);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid address data or duplicate record");
        }
    }

    public Address updateAddress(Address address) {
        if (address.getId() == null || !addressRepository.existsById(address.getId())) {
            throw new ResourceNotFoundException("Address not found");
        }
        if (address.getMobileNumber() == null || address.getMobileNumber().trim().isEmpty()) {
            throw new BadRequestException("Mobile number is required for an address");
        }
        try {
            return addressRepository.save(address);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Invalid address data");
        }
    }

    public void deleteAddress(String addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new ResourceNotFoundException("Address not found");
        }
        addressRepository.deleteById(addressId);
    }

    public Optional<Address> getAddressById(String addressId) {
        return addressRepository.findById(addressId);
    }
}
