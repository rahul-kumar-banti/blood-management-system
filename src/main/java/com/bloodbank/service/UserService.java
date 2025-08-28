package com.bloodbank.service;

import com.bloodbank.entity.User;
import com.bloodbank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Current user not found");
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setBloodType(userDetails.getBloodType());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, com.bloodbank.dto.UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBloodType() != null) {
            user.setBloodType(request.getBloodType());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }
        
        return userRepository.save(user);
    }
    
    public User updateCurrentUserProfile(User userDetails) {
        User currentUser = getCurrentUser();
        
        currentUser.setFirstName(userDetails.getFirstName());
        currentUser.setLastName(userDetails.getLastName());
        currentUser.setEmail(userDetails.getEmail());
        currentUser.setPhoneNumber(userDetails.getPhoneNumber());
        currentUser.setBloodType(userDetails.getBloodType());
        
        return userRepository.save(currentUser);
    }
    
    public User updateCurrentUserProfile(com.bloodbank.dto.UserUpdateRequest request) {
        User currentUser = getCurrentUser();
        
        if (request.getFirstName() != null) {
            currentUser.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            currentUser.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBloodType() != null) {
            currentUser.setBloodType(request.getBloodType());
        }
        
        return userRepository.save(currentUser);
    }
    
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public void changeCurrentUserPassword(String oldPassword, String newPassword) {
        User currentUser = getCurrentUser();
        changePassword(currentUser.getId(), oldPassword, newPassword);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }
    
    public List<User> searchUsers(String name, String bloodType, String role, Boolean active) {
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .filter(user -> name == null || 
                        user.getFirstName().toLowerCase().contains(name.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(name.toLowerCase()) ||
                        user.getUsername().toLowerCase().contains(name.toLowerCase()))
                .filter(user -> bloodType == null || 
                        user.getBloodType() != null && 
                        user.getBloodType().name().equalsIgnoreCase(bloodType))
                .filter(user -> role == null || 
                        user.getRole().name().equalsIgnoreCase(role))
                .filter(user -> active == null || user.isActive() == active)
                .collect(Collectors.toList());
    }
    
    public List<User> searchUsersAdvanced(com.bloodbank.dto.UserSearchRequest request) {
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .filter(user -> request.getName() == null || 
                        user.getFirstName().toLowerCase().contains(request.getName().toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(request.getName().toLowerCase()) ||
                        user.getUsername().toLowerCase().contains(request.getName().toLowerCase()))
                .filter(user -> request.getBloodType() == null || 
                        user.getBloodType() != null && 
                        user.getBloodType() == request.getBloodType())
                .filter(user -> request.getRole() == null || 
                        user.getRole() == request.getRole())
                .filter(user -> request.getIsActive() == null || user.isActive() == request.getIsActive())
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        List<User> allUsers = userRepository.findAll();
        List<User> activeUsers = userRepository.findAllActiveUsers();
        
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", activeUsers.size());
        stats.put("inactiveUsers", allUsers.size() - activeUsers.size());
        
        // Count by role
        Map<String, Long> roleCounts = allUsers.stream()
                .collect(Collectors.groupingBy(user -> user.getRole().name(), Collectors.counting()));
        stats.put("usersByRole", roleCounts);
        
        // Count by blood type
        Map<String, Long> bloodTypeCounts = allUsers.stream()
                .filter(user -> user.getBloodType() != null)
                .collect(Collectors.groupingBy(user -> user.getBloodType().name(), Collectors.counting()));
        stats.put("usersByBloodType", bloodTypeCounts);
        
        // Count active donors
        long activeDonors = allUsers.stream()
                .filter(user -> user.getRole() == User.Role.DONOR && user.isActive())
                .count();
        stats.put("activeDonors", activeDonors);
        
        return stats;
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllActiveDonors() {
        return userRepository.findAllActiveDonors();
    }
    
    public List<User> getDonorsByBloodType(User.BloodType bloodType) {
        return userRepository.findDonorsByBloodType(bloodType);
    }
    
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        return userRepository.save(user);
    }
    
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        return userRepository.save(user);
    }
    
    public int bulkActivateUsers(List<Long> userIds) {
        int activatedCount = 0;
        for (Long id : userIds) {
            try {
                activateUser(id);
                activatedCount++;
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Failed to activate user " + id + ": " + e.getMessage());
            }
        }
        return activatedCount;
    }
    
    public int bulkDeactivateUsers(List<Long> userIds) {
        int deactivatedCount = 0;
        for (Long id : userIds) {
            try {
                deactivateUser(id);
                deactivatedCount++;
            } catch (Exception e) {
                // Log error but continue with other users
                System.err.println("Failed to deactivate user " + id + ": " + e.getMessage());
            }
        }
        return deactivatedCount;
    }
    
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
