package com.bloodbank.service;

import com.bloodbank.entity.BloodInventory;
import com.bloodbank.repository.BloodInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.bloodbank.entity.User.BloodType;

@Service
@RequiredArgsConstructor
public class BloodInventoryService {
    
    private final BloodInventoryRepository bloodInventoryRepository;
    
    public BloodInventory addBloodUnit(BloodInventory bloodInventory) {
        return bloodInventoryRepository.save(bloodInventory);
    }
    
    public BloodInventory updateBloodUnit(Long id, BloodInventory bloodInventoryDetails) {
        BloodInventory bloodInventory = bloodInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found"));
        
        bloodInventory.setBloodType(bloodInventoryDetails.getBloodType());
        bloodInventory.setQuantity(bloodInventoryDetails.getQuantity());
        bloodInventory.setUnitOfMeasure(bloodInventoryDetails.getUnitOfMeasure());
        bloodInventory.setExpiryDate(bloodInventoryDetails.getExpiryDate());
        bloodInventory.setStatus(bloodInventoryDetails.getStatus());
        bloodInventory.setNotes(bloodInventoryDetails.getNotes());
        
        return bloodInventoryRepository.save(bloodInventory);
    }
    
    public void removeBloodUnit(Long id, Integer quantity) {
        BloodInventory bloodInventory = bloodInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found"));
        
        if (bloodInventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient blood quantity");
        }
        
        bloodInventory.setQuantity(bloodInventory.getQuantity() - quantity);
        
        if (bloodInventory.getQuantity() == 0) {
            bloodInventory.setStatus(BloodInventory.Status.DISCARDED);
        }
        
        bloodInventoryRepository.save(bloodInventory);
    }
    
    public BloodInventory getBloodInventoryById(Long id) {
        return bloodInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found"));
    }
    
    public List<BloodInventory> getAllBloodInventory() {
        return bloodInventoryRepository.findAll();
    }
    
    public List<BloodInventory> getBloodInventoryByType(BloodType bloodType) {
        return bloodInventoryRepository.findByBloodType(bloodType);
    }
    
    public List<BloodInventory> getAvailableInventory() {
        return bloodInventoryRepository.findAvailableInventory(LocalDateTime.now());
    }
    
    public List<BloodInventory> getExpiredInventory() {
        return bloodInventoryRepository.findExpiredInventory(LocalDateTime.now());
    }
    
    public List<BloodInventory> getAvailableByBloodType(BloodType bloodType) {
        return bloodInventoryRepository.findAvailableByBloodType(bloodType, LocalDateTime.now());
    }
    
    public Integer getTotalAvailableQuantityByBloodType(BloodType bloodType) {
        return bloodInventoryRepository.getTotalAvailableQuantityByBloodType(bloodType, LocalDateTime.now());
    }
    
    public void checkAndUpdateExpiredInventory() {
        List<BloodInventory> expiredInventory = getExpiredInventory();
        for (BloodInventory inventory : expiredInventory) {
            inventory.setStatus(BloodInventory.Status.EXPIRED);
            bloodInventoryRepository.save(inventory);
        }
    }
}
