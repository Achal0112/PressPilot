package com.example.presspilot.model;

public class Delivery {

    private String deliveryId;
    private String customerId;
    private String customerName;
    private String mobile;
    private String date;
    private String day;
    private String status;
    private long deliveryTime;

    public Delivery() {
    }

    public Delivery(String deliveryId,
                    String customerId,
                    String customerName,
                    String mobile,
                    String date,
                    String day,
                    String status,
                    long deliveryTime) {

        this.deliveryId = deliveryId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.mobile = mobile;
        this.date = date;
        this.day = day;
        this.status = status;
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}