package com.bloodbank.controller;

import com.bloodbank.dto.PasswordChangeRequest;
import com.bloodbank.dto.UserSearchRequest;
import com.bloodbank.dto.UserUpdateRequest;
import com.bloodbank.entity.User;
import com.bloodbank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
    
    @GetMapping("/donors")
    public ResponseEntity<List<User>> getAllDonors() {
        List<User> donors = userService.getAllActiveDonors();
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/donors/{bloodType}")
    public ResponseEntity<List<User>> getDonorsByBloodType(@PathVariable String bloodType) {
        try {
            User.BloodType type = User.BloodType.valueOf(bloodType.toUpperCase());
            List<User> donors = userService.getDonorsByBloodType(type);
            return ResponseEntity.ok(donors);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        List<User> users = userService.searchUsers(name, bloodType, role, active);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<User>> searchUsersAdvanced(@RequestBody UserSearchRequest request) {
        List<User> users = userService.searchUsersAdvanced(request);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody UserUpdateRequest request) {
        User updatedUser = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long id, 
            @RequestBody PasswordChangeRequest request) {
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
    
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changeCurrentUserPassword(@RequestBody PasswordChangeRequest request) {
        userService.changeCurrentUserPassword(request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        User activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(activatedUser);
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        User deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(deactivatedUser);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/bulk-activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> bulkActivateUsers(@RequestBody List<Long> userIds) {
        int activatedCount = userService.bulkActivateUsers(userIds);
        return ResponseEntity.ok(Map.of("message", activatedCount + " users activated successfully"));
    }
    
    @PostMapping("/bulk-deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> bulkDeactivateUsers(@RequestBody List<Long> userIds) {
        int deactivatedCount = userService.bulkDeactivateUsers(userIds);
        return ResponseEntity.ok(Map.of("message", deactivatedCount + " users deactivated successfully"));
    }
    
    @GetMapping("/validate-username/{username}")
    public ResponseEntity<Map<String, Boolean>> validateUsername(@PathVariable String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
    
    @GetMapping("/validate-email/{email}")
    public ResponseEntity<Map<String, Boolean>> validateEmail(@PathVariable String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
    
    @GetMapping("/roles")
    public ResponseEntity<User.Role[]> getAvailableRoles() {
        return ResponseEntity.ok(User.Role.values());
    }
    
    @GetMapping("/blood-types")
    public ResponseEntity<User.BloodType[]> getAvailableBloodTypes() {
        return ResponseEntity.ok(User.BloodType.values());
    }
}
