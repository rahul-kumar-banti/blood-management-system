package com.bloodbank.controller;

import com.bloodbank.entity.BloodInventory;
import com.bloodbank.service.BloodInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bloodbank.entity.User.BloodType;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class BloodInventoryController {
    
    private final BloodInventoryService bloodInventoryService;
    
    @GetMapping
    public ResponseEntity<List<BloodInventory>> getAllInventory() {
        List<BloodInventory> inventory = bloodInventoryService.getAllBloodInventory();
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BloodInventory> getInventoryById(@PathVariable Long id) {
        BloodInventory inventory = bloodInventoryService.getBloodInventoryById(id);
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping("/type/{bloodType}")
    public ResponseEntity<List<BloodInventory>> getInventoryByBloodType(@PathVariable String bloodType) {
        try {
            BloodType type = BloodType.valueOf(bloodType.toUpperCase());
            List<BloodInventory> inventory = bloodInventoryService.getBloodInventoryByType(type);
            return ResponseEntity.ok(inventory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<BloodInventory>> getAvailableInventory() {
        List<BloodInventory> inventory = bloodInventoryService.getAvailableInventory();
        return ResponseEntity.ok(inventory);
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'NURSE')")
    public ResponseEntity<BloodInventory> addBloodUnit(@RequestBody BloodInventory bloodInventory) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Adding blood unit. User: {}, Authorities: {}, Principal: {}", 
                 auth.getName(), auth.getAuthorities(), auth.getPrincipal());
        
        BloodInventory saved = bloodInventoryService.addBloodUnit(bloodInventory);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN', 'NURSE')")
    public ResponseEntity<BloodInventory> updateBloodUnit(@PathVariable Long id, @RequestBody BloodInventory bloodInventoryDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Updating blood unit. User: {}, Authorities: {}, Principal: {}", 
                 auth.getName(), auth.getAuthorities(), auth.getPrincipal());
        
        BloodInventory updated = bloodInventoryService.updateBloodUnit(id, bloodInventoryDetails);
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/{id}/remove")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<Void> removeBloodUnit(@PathVariable Long id, @RequestParam Integer quantity) {
        bloodInventoryService.removeBloodUnit(id, quantity);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<List<BloodInventory>> getExpiredInventory() {
        List<BloodInventory> expired = bloodInventoryService.getExpiredInventory();
        return ResponseEntity.ok(expired);
    }
}
