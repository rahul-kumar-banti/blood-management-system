package com.bloodbank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", nullable = false)
    @NotNull(message = "Blood type is required")
    private User.BloodType bloodType;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_of_measure")
    private String unitOfMeasure = "ml";
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "collection_date")
    private LocalDateTime collectionDate;
    
    @Column(name = "donor_id")
    private Long donorId;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.AVAILABLE;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Status {
        AVAILABLE, RESERVED, EXPIRED, DISCARDED, IN_TRANSIT
    }
}
