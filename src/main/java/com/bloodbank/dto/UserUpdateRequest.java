package com.bloodbank.dto;

import com.bloodbank.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private User.BloodType bloodType;
    private User.Role role;
    private Boolean isActive;
}
