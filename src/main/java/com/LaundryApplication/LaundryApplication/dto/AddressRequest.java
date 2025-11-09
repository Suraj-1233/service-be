package com.LaundryApplication.LaundryApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    // optional pattern example for 10 digit mobile (adjust per your requirement)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    private String flatBuilding;

    private String areaStreet;
    private String city;
    private String pincode;
    private String label;
    private Double latitude;
    private Double longitude;
    private boolean isDefault;
}
