package com.bloodbank.client;

public class Donation {
    private Long id;
    private String donor;
    private String bloodType;
    private int quantity;
    private String unit;
    private String status;
    private String donationDate;
    private String notes;
    private String createdAt;
    private String updatedAt;
    
    public Donation() {}
    
    public Donation(Long id, String donor, String bloodType, int quantity, String unit, 
                    String status, String donationDate, String notes, String createdAt, String updatedAt) {
        this.id = id;
        this.donor = donor;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.unit = unit;
        this.status = status;
        this.donationDate = donationDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDonor() { return donor; }
    public void setDonor(String donor) { this.donor = donor; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDonationDate() { return donationDate; }
    public void setDonationDate(String donationDate) { this.donationDate = donationDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
