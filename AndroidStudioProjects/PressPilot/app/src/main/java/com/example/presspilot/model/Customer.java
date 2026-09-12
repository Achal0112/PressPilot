package com.example.presspilot.model;

public class Customer {

    private String customerId;
    private String name;
    private String mobile;
    private String address;
    private String newspaperType;
    private double pricePerDay;
    private long createdAt;

    // Required empty constructor for Firebase
    public Customer() {
    }

    public Customer(String customerId,
                    String name,
                    String mobile,
                    String address,
                    String newspaperType,
                    double pricePerDay,
                    long createdAt) {

        this.customerId = customerId;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.newspaperType = newspaperType;
        this.pricePerDay = pricePerDay;
        this.createdAt = createdAt;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNewspaperType() {
        return newspaperType;
    }

    public void setNewspaperType(String newspaperType) {
        this.newspaperType = newspaperType;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}