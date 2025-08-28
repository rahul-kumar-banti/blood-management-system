package com.bloodbank.client;

public class BloodInventory {
    private Long id;
    private String bloodType;
    private int quantity;
    private String unit;
    private String status;
    private String expiryDate;
    private String batchNumber;
    private String createdAt;
    private String updatedAt;
    
    public BloodInventory() {}
    
    public BloodInventory(Long id, String bloodType, int quantity, String unit, String status, 
                         String expiryDate, String batchNumber, String createdAt, String updatedAt) {
        this.id = id;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.unit = unit;
        this.status = status;
        this.expiryDate = expiryDate;
        this.batchNumber = batchNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
