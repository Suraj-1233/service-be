package com.LaundryApplication.LaundryApplication.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "addresses")
public class Address {

    @Id
    private String id;

    private String userId;
    private String fullName;        // 🆕 matches Flutter model
    private String mobileNumber;    // required field
    private String flatBuilding;    // 🆕 house/flat info
    private String areaStreet;      // 🆕 area or street
    private String city;
    private String pincode;
    private String label;           // Home / Work / Other
    private Double latitude;        // 🆕 optional
    private Double longitude;       // 🆕 optional
    private boolean isDefault;

    // ===== Getters & Setters =====
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFlatBuilding() {
        return flatBuilding;
    }

    public void setFlatBuilding(String flatBuilding) {
        this.flatBuilding = flatBuilding;
    }

    public String getAreaStreet() {
        return areaStreet;
    }

    public void setAreaStreet(String areaStreet) {
        this.areaStreet = areaStreet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", flatBuilding='" + flatBuilding + '\'' +
                ", areaStreet='" + areaStreet + '\'' +
                ", city='" + city + '\'' +
                ", pincode='" + pincode + '\'' +
                ", label='" + label + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isDefault=" + isDefault +
                '}';
    }
}
