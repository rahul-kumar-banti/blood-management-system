package com.bloodbank.repository;

import com.bloodbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DONOR' AND u.isActive = true")
    List<User> findAllActiveDonors();
    
    @Query("SELECT u FROM User u WHERE u.bloodType = ?1 AND u.role = 'DONOR' AND u.isActive = true")
    List<User> findDonorsByBloodType(User.BloodType bloodType);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
}
