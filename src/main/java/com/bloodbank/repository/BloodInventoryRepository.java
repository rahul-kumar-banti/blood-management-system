package com.bloodbank.repository;

import com.bloodbank.entity.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.bloodbank.entity.User.BloodType;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
    
    List<BloodInventory> findByBloodType(BloodType bloodType);
    
    List<BloodInventory> findByStatus(BloodInventory.Status status);
    
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.status = 'AVAILABLE' AND bi.expiryDate > ?1")
    List<BloodInventory> findAvailableInventory(LocalDateTime currentDate);
    
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.expiryDate <= ?1")
    List<BloodInventory> findExpiredInventory(LocalDateTime currentDate);
    
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.bloodType = ?1 AND bi.status = 'AVAILABLE' AND bi.expiryDate > ?2")
    List<BloodInventory> findAvailableByBloodType(BloodType bloodType, LocalDateTime currentDate);
    
    @Query("SELECT SUM(bi.quantity) FROM BloodInventory bi WHERE bi.bloodType = ?1 AND bi.status = 'AVAILABLE' AND bi.expiryDate > ?2")
    Integer getTotalAvailableQuantityByBloodType(BloodType bloodType, LocalDateTime currentDate);
}
