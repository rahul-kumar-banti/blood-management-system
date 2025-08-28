package com.bloodbank.service;

import com.bloodbank.dto.AuthRequest;
import com.bloodbank.dto.AuthResponse;
import com.bloodbank.dto.RegisterRequest;
import com.bloodbank.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .bloodType(request.getBloodType())
                .isActive(true)
                .build();
        
        User savedUser = userService.createUser(user);
        
        String token = jwtService.generateToken(savedUser);
        
        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .build();
    }
    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
    
    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token, userService.loadUserByUsername(jwtService.extractUsername(token)));
        } catch (Exception e) {
            return false;
        }
    }
}
