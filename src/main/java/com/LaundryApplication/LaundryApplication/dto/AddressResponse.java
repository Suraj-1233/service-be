package com.LaundryApplication.LaundryApplication.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private String id;
    private String userId;
    private String fullName;
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
