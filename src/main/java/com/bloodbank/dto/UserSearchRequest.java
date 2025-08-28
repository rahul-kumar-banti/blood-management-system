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
public class UserSearchRequest {
    
    private String name;
    private User.BloodType bloodType;
    private User.Role role;
    private Boolean isActive;
    private Integer page;
    private Integer size;
}
