package com.bloodbank.client;

public class Request {
    private Long id;
    private String hospital;
    private String patient;
    private String bloodType;
    private int quantity;
    private String unit;
    private String priority;
    private String status;
    private String requestDate;
    private String notes;
    private String createdAt;
    private String updatedAt;
    
    public Request() {}
    
    public Request(Long id, String hospital, String patient, String bloodType, int quantity, 
                   String unit, String priority, String status, String requestDate, String notes, 
                   String createdAt, String updatedAt) {
        this.id = id;
        this.hospital = hospital;
        this.patient = patient;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.unit = unit;
        this.priority = priority;
        this.status = status;
        this.requestDate = requestDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
