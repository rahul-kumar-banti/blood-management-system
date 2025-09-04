# Blood Bank Management System - Login Screen Implementation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Backend Implementation](#backend-implementation)
3. [Frontend Implementation](#frontend-implementation)
4. [Database Schema](#database-schema)
5. [Security Features](#security-features)
6. [API Endpoints](#api-endpoints)
7. [Usage Examples](#usage-examples)
8. [Testing](#testing)
9. [Troubleshooting](#troubleshooting)

## 🎯 Overview

The login screen is the entry point to the Blood Bank Management System, providing secure authentication using JWT (JSON Web Token) technology. The system supports multiple user roles with different access levels and implements industry-standard security practices.

### Key Features
- **JWT-based Authentication**: Secure token-based authentication
- **Role-based Access Control**: Different permissions for different user types
- **Password Security**: BCrypt hashing with salt
- **Session Management**: Configurable token expiry
- **Cross-platform Support**: Works on Windows, macOS, and Linux

## 🔧 Backend Implementation

### 1. Authentication Controller

The `AuthController` handles all authentication-related HTTP requests:

```java
package com.bloodbank.controller;

import com.bloodbank.dto.AuthRequest;
import com.bloodbank.dto.AuthResponse;
import com.bloodbank.dto.RegisterRequest;
import com.bloodbank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
    
    @GetMapping("/hash/{password}")
    public ResponseEntity<String> generateHash(@PathVariable String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        return ResponseEntity.ok(hash);
    }
}
```

### 2. Authentication Service

The `AuthService` contains the business logic for authentication:

```java
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
```

### 3. JWT Service

The `JwtService` handles JWT token generation and validation:

```java
package com.bloodbank.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### 4. Security Configuration

Spring Security configuration for authentication:

```java
package com.bloodbank.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/health/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
        
        // For H2 console
        http.headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 5. JWT Authentication Filter

Filter for processing JWT tokens in requests:

```java
package com.bloodbank.config;

import com.bloodbank.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Skip JWT processing for authentication endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/auth/") || requestURI.contains("/public/")) {
            log.debug("Skipping JWT processing for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        log.debug("Processing request: {} with Authorization header: {}", requestURI, authHeader);
        
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        log.debug("Extracted username: {} from JWT for URI: {}", username, requestURI);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.debug("Loaded user details for username: {} with authorities: {}", username, userDetails.getAuthorities());
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Set authentication context for user: {} with authorities: {}", username, userDetails.getAuthorities());
            } else {
                log.warn("JWT token is not valid for user: {}", username);
            }
        } else if (username == null) {
            log.warn("Could not extract username from JWT token");
        } else {
            log.debug("Authentication context already exists for user: {}", username);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 6. Data Transfer Objects (DTOs)

#### AuthRequest DTO
```java
package com.bloodbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
```

#### AuthResponse DTO
```java
package com.bloodbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
```

## 🖥️ Frontend Implementation

### 1. Swing Client Main Class

The main Swing application with login functionality:

```java
package com.bloodbank.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableRowSorter;
import javax.swing.ListSelectionModel;
import java.util.List;
import java.awt.Dimension;

public class BloodBankSwingClient extends JFrame {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private String authToken;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel;
    private JPanel inventoryPanel;
    private JPanel usersPanel;
    private JPanel donationsPanel;
    private JPanel requestsPanel;
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private String currentUsername;
    
    private HttpClient httpClient;
    
    // Login form components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Other components...
    
    public BloodBankSwingClient() {
        httpClient = HttpClient.newHttpClient();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Blood Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create login panel
        createLoginPanel();
        
        // Create main tabbed pane
        createMainTabs();
        
        // Show login panel initially
        setContentPane(loginPanel);
        setVisible(true);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("Blood Bank Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(titleLabel, gbc);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        
        loginPanel.add(usernameLabel, gbc);
        loginPanel.add(usernameField, gbc);
        loginPanel.add(passwordLabel, gbc);
        loginPanel.add(passwordField, gbc);
        loginPanel.add(loginButton, gbc);
        
        // Add sample credentials info
        JLabel infoLabel = new JLabel("Sample: admin/admin123, doctor1/doctor123, nurse1/nurse123");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(infoLabel, gbc);
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            performLogin(username, password);
        });
    }
    
    private void performLogin(String username, String password) {
        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build();
        
        CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request, 
                HttpResponse.BodyHandlers.ofString());
        
        future.thenAccept(response -> {
            if (response.statusCode() == 200) {
                // Parse token from response (simplified)
                authToken = "Bearer " + response.body().split("\"token\":\"")[1].split("\"")[0];
                currentUsername = username;
                SwingUtilities.invokeLater(() -> {
                    setContentPane(mainPanel);
                    welcomeLabel.setText("Welcome, " + currentUsername + "!");
                    revalidate();
                    repaint();
                    
                    // Set auth token for services
                    if (userService != null) {
                        userService.setAuthToken(authToken);
                    }
                    if (bloodInventoryService != null) {
                        bloodInventoryService.setAuthToken(authToken);
                    }
                    
                    JOptionPane.showMessageDialog(this, "Login successful!");
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Login failed: " + response.body(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).exceptionally(e -> {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            });
            return null;
        });
    }
    
    private void performLogout() {
        authToken = null;
        currentUsername = null;
        SwingUtilities.invokeLater(() -> {
            setContentPane(loginPanel);
            clearLoginForm();
            revalidate();
            repaint();
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
        });
    }
    
    private void clearLoginForm() {
        usernameField.setText("");
        passwordField.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankSwingClient());
    }
}
```

### 2. Main Tabs Creation

```java
private void createMainTabs() {
    // Create top panel with welcome message and logout button
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
    ));
    topPanel.setBackground(new Color(245, 245, 245));
    
    // Welcome label (will be updated when user logs in)
    welcomeLabel = new JLabel("Welcome!");
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
    welcomeLabel.setForeground(new Color(50, 50, 50));
    topPanel.add(welcomeLabel, BorderLayout.WEST);
    
    JButton logoutButton = new JButton("Logout");
    logoutButton.setPreferredSize(new Dimension(100, 30));
    logoutButton.setBackground(new Color(220, 53, 69)); // Red color for logout
    logoutButton.setForeground(Color.WHITE);
    logoutButton.setFocusPainted(false);
    logoutButton.setBorder(BorderFactory.createRaisedBevelBorder());
    logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
    topPanel.add(logoutButton, BorderLayout.EAST);
    
    // Add logout functionality
    logoutButton.addActionListener(e -> performLogout());
    
    // Create main panel to hold both top panel and tabbed pane
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(topPanel, BorderLayout.NORTH);
    
    tabbedPane = new JTabbedPane();
    
    // Create inventory panel
    createInventoryPanel();
    
    // Create users panel
    createUsersPanel();
    
    // Create donations panel
    createDonationsPanel();
    
    // Create requests panel
    createRequestsPanel();
    
    tabbedPane.addTab("Blood Inventory", inventoryPanel);
    tabbedPane.addTab("Users", usersPanel);
    tabbedPane.addTab("Donations", donationsPanel);
    tabbedPane.addTab("Requests", requestsPanel);
    
    mainPanel.add(tabbedPane, BorderLayout.CENTER);
}
```

## 🗄️ Database Schema

### Users Table

```sql
-- 1. USERS TABLE
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    blood_type VARCHAR(10),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users table indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_blood_type ON users(blood_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Users table constraints
ALTER TABLE users ADD CONSTRAINT chk_users_username_length 
    CHECK (LENGTH(username) >= 3 AND LENGTH(username) <= 50);
ALTER TABLE users ADD CONSTRAINT chk_users_password_length 
    CHECK (LENGTH(password) >= 6);
ALTER TABLE users ADD CONSTRAINT chk_users_role_valid 
    CHECK (role IN ('ADMIN', 'STAFF', 'DONOR', 'PATIENT'));
ALTER TABLE users ADD CONSTRAINT chk_users_blood_type_valid 
    CHECK (blood_type IN ('A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 
                         'AB_POSITIVE', 'AB_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE'));
```

### Sample User Data

```sql
-- Insert sample users
INSERT INTO users (username, email, password, first_name, last_name, role, blood_type, is_active) VALUES 
('admin', 'admin@bloodbank.com', '$2a$10$encrypted_password_hash', 'System', 'Administrator', 'ADMIN', 'O_POSITIVE', true),
('staff1', 'staff1@bloodbank.com', '$2a$10$encrypted_password_hash', 'John', 'Staff', 'STAFF', 'A_POSITIVE', true),
('staff2', 'staff2@bloodbank.com', '$2a$10$encrypted_password_hash', 'Jane', 'Nurse', 'STAFF', 'B_POSITIVE', true),
('donor1', 'donor1@email.com', '$2a$10$encrypted_password_hash', 'Mike', 'Donor', 'DONOR', 'O_POSITIVE', true),
('donor2', 'donor2@email.com', '$2a$10$encrypted_password_hash', 'Sarah', 'Donor', 'DONOR', 'A_NEGATIVE', true),
('patient1', 'patient1@email.com', '$2a$10$encrypted_password_hash', 'Bob', 'Patient', 'PATIENT', 'B_POSITIVE', true);
```

## 🔒 Security Features

### 1. Password Security
- **BCrypt Hashing**: Passwords are hashed using BCrypt with salt
- **Minimum Length**: 6 characters minimum
- **Secure Storage**: Passwords are never stored in plain text

### 2. JWT Token Security
- **Secret Key**: Base64-encoded secret key for signing tokens
- **Expiration**: Configurable token expiry (default: 24 hours)
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Stateless**: No server-side session storage

### 3. Role-Based Access Control
- **User Roles**: ADMIN, STAFF, DONOR, PATIENT
- **Endpoint Protection**: Different endpoints accessible by different roles
- **Method-Level Security**: @PreAuthorize annotations for fine-grained control

### 4. Input Validation
- **Username Validation**: 3-50 characters, unique
- **Email Validation**: Valid email format, unique
- **Password Validation**: Minimum 6 characters
- **Role Validation**: Must be one of predefined roles

## 🌐 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/login` | User login | `{"username": "string", "password": "string"}` | `{"token": "string", "username": "string", "role": "string"}` |
| POST | `/api/auth/register` | User registration | `RegisterRequest` | `{"token": "string", "username": "string", "role": "string"}` |
| POST | `/api/auth/validate` | Token validation | Header: `Authorization: Bearer <token>` | `boolean` |
| GET | `/api/auth/hash/{password}` | Generate password hash | Path parameter | `string` |

### Request/Response Examples

#### Login Request
```json
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}
```

#### Login Response
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzNzI5NjAwMCwiZXhwIjoxNjM3Mzg0MDAwfQ.signature",
    "username": "admin",
    "role": "ADMIN"
}
```

#### Token Validation
```http
POST /api/auth/validate
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzNzI5NjAwMCwiZXhwIjoxNjM3Mzg0MDAwfQ.signature
```

## 🚀 Usage Examples

### 1. Starting the Application

#### Backend (Spring Boot)
```bash
# Navigate to project directory
cd blood_bank

# Start the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`

#### Frontend (Swing Client)
```bash
# On macOS/Linux
chmod +x run-swing-client.sh
./run-swing-client.sh

# On Windows
run-swing-client.bat
```

### 2. Login Process

1. **Launch the Swing Client**
   - The application starts with a login screen
   - Enter username and password
   - Click "Login" button

2. **Authentication Flow**
   - Client sends credentials to `/api/auth/login`
   - Backend validates credentials
   - JWT token generated and returned
   - Client stores token for subsequent requests

3. **Main Application**
   - After successful login, main interface appears
   - Welcome message shows logged-in username
   - Logout button available in top-right corner

### 3. Default Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Administrator |
| `staff1` | `staff123` | Staff |
| `staff2` | `staff123` | Staff |
| `donor1` | `donor123` | Donor |
| `donor2` | `donor123` | Donor |
| `patient1` | `patient123` | Patient |

## 🧪 Testing

### 1. Manual Testing

#### Login Testing
1. Start both backend and frontend
2. Try valid credentials (should succeed)
3. Try invalid credentials (should show error)
4. Test with different user roles
5. Verify token expiration

#### Security Testing
1. Try accessing protected endpoints without token
2. Test with expired tokens
3. Verify role-based access control
4. Test input validation

### 2. API Testing with cURL

#### Login Test
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### Token Validation Test
```bash
# First get token from login
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzNzI5NjAwMCwiZXhwIjoxNjM3Mzg0MDAwfQ.signature"

# Then validate it
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Unit Testing

#### AuthService Test
```java
@SpringBootTest
class AuthServiceTest {
    
    @Autowired
    private AuthService authService;
    
    @Test
    void testSuccessfulLogin() {
        AuthRequest request = new AuthRequest("admin", "admin123");
        AuthResponse response = authService.login(request);
        
        assertNotNull(response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }
    
    @Test
    void testInvalidCredentials() {
        AuthRequest request = new AuthRequest("admin", "wrongpassword");
        
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });
    }
}
```

## 🔧 Configuration

### 1. Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/bloodbank
spring.datasource.username=rahul
spring.datasource.password=8004
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# JWT Configuration
jwt.secret=ZmQ0ZGI5NjQ0MDQwY2I4MjUxY2YwZmQ3OGNhYzRkNTZmZDRkYjk2NDQwNDBjYjgyNTFjZjBmZDc4Y2FjNGQ1Ng==
jwt.expiration=86400000

# Logging Configuration
logging.level.com.bloodbank=DEBUG
logging.level.org.springframework.security=DEBUG

# Validation Configuration
spring.validation.enabled=true
```

### 2. JWT Configuration

- **Secret Key**: Base64-encoded 256-bit key
- **Expiration**: 24 hours (86,400,000 milliseconds)
- **Algorithm**: HS256 (HMAC with SHA-256)

## 🐛 Troubleshooting

### Common Issues

#### 1. Login Fails
- **Check backend**: Ensure Spring Boot application is running
- **Check database**: Verify PostgreSQL is running and accessible
- **Check credentials**: Use correct username/password
- **Check logs**: Look for authentication errors in console

#### 2. Token Validation Fails
- **Check token format**: Ensure "Bearer " prefix is included
- **Check expiration**: Token may have expired
- **Check secret key**: Verify JWT secret is consistent

#### 3. Frontend Connection Issues
- **Check URL**: Verify BASE_URL in Swing client
- **Check CORS**: Ensure backend allows cross-origin requests
- **Check network**: Verify localhost:8080 is accessible

#### 4. Database Connection Issues
- **Check PostgreSQL**: Ensure service is running
- **Check credentials**: Verify username/password in application.properties
- **Check database**: Ensure 'bloodbank' database exists

### Debug Steps

1. **Enable Debug Logging**
   ```properties
   logging.level.com.bloodbank=DEBUG
   logging.level.org.springframework.security=DEBUG
   ```

2. **Check Application Logs**
   - Look for authentication attempts
   - Check for JWT processing errors
   - Verify database queries

3. **Test API Endpoints**
   - Use cURL or Postman to test endpoints directly
   - Verify request/response format
   - Check HTTP status codes

4. **Verify Database**
   ```sql
   -- Check if users table exists
   \dt users;
   
   -- Check if sample users exist
   SELECT username, role, is_active FROM users;
   ```

## 📚 Additional Resources

### Related Documentation
- [API Documentation](API_DOCUMENTATION.md)
- [User Manual](USER_MANUAL.md)
- [Deployment Guide](DEPLOYMENT_GUIDE.md)
- [Database Schema](DATABASE_INIT_SCRIPT.sql)

### Technical References
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - JWT token decoder and validator
- [BCrypt Calculator](https://bcrypt.online/) - Password hash generator
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Best Practices
- **Security**: Always use HTTPS in production
- **Passwords**: Implement password complexity requirements
- **Tokens**: Use short expiration times for sensitive applications
- **Logging**: Log authentication attempts for security monitoring
- **Validation**: Validate all input on both client and server side

---

*This document provides a comprehensive guide to implementing and using the login screen in the Blood Bank Management System. For additional support or questions, please refer to the main README or contact the development team.*
