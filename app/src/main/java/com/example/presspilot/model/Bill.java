package com.example.presspilot.model;

public class Bill {

    private String billId;
    private String customerId;
    private String customerName;
    private String month;
    private int deliveredDays;
    private double pricePerDay;
    private double totalAmount;
    private long createdAt;

    public Bill() {
        // Required empty constructor for Firestore
    }

    public Bill(String billId,
                String customerId,
                String customerName,
                String month,
                int deliveredDays,
                double pricePerDay,
                double totalAmount,
                long createdAt) {

        this.billId = billId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.month = month;
        this.deliveredDays = deliveredDays;
        this.pricePerDay = pricePerDay;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getDeliveredDays() {
        return deliveredDays;
    }

    public void setDeliveredDays(int deliveredDays) {
        this.deliveredDays = deliveredDays;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}