package com.bloodbank.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BloodInventoryService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private String authToken;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public List<BloodInventory> getAllInventory() throws Exception {
        if (authToken == null) {
            throw new RuntimeException("Authentication token not set");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/inventory"))
                .header("Authorization", authToken)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseInventoryListFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to fetch inventory: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public BloodInventory createInventory(BloodInventory inventory) throws Exception {
        if (authToken == null) {
            throw new RuntimeException("Authentication token not set");
        }
        
        String json = inventoryToJson(inventory);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/inventory/add"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return parseInventoryFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to create inventory: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public BloodInventory updateInventory(Long id, BloodInventory inventory) throws Exception {
        if (authToken == null) {
            throw new RuntimeException("Authentication token not set");
        }
        
        String json = inventoryToJson(inventory);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/inventory/" + id))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseInventoryFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to update inventory: " + response.statusCode() + " - " + response.body());
        }
    }
    
    public void deleteInventory(Long id) throws Exception {
        if (authToken == null) {
            throw new RuntimeException("Authentication token not set");
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/inventory/" + id))
                .header("Authorization", authToken)
                .DELETE()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete inventory: " + response.statusCode() + " - " + response.body());
        }
    }
    
    // Helper methods for JSON parsing
    private List<BloodInventory> parseInventoryListFromJson(String json) {
        List<BloodInventory> inventory = new ArrayList<>();
        Pattern inventoryPattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = inventoryPattern.matcher(json);
        
        while (matcher.find()) {
            String inventoryJson = matcher.group();
            try {
                BloodInventory item = parseInventoryFromJson(inventoryJson);
                inventory.add(item);
            } catch (Exception e) {
                // Skip invalid inventory entries
            }
        }
        return inventory;
    }
    
    private BloodInventory parseInventoryFromJson(String json) {
        BloodInventory inventory = new BloodInventory();
        inventory.setId(extractLong(json, "id"));
        inventory.setBloodType(extractString(json, "bloodType"));
        inventory.setQuantity(extractInt(json, "quantity"));
        inventory.setUnit(extractString(json, "unit"));
        inventory.setStatus(extractString(json, "status"));
        inventory.setExpiryDate(extractString(json, "expiryDate"));
        inventory.setBatchNumber(extractString(json, "batchNumber"));
        inventory.setCreatedAt(extractString(json, "createdAt"));
        inventory.setUpdatedAt(extractString(json, "updatedAt"));
        return inventory;
    }
    
    /**
     * Converts a date string to ISO datetime format for the backend
     */
    private String formatDateForBackend(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Parse the date string (YYYY-MM-DD format)
            LocalDate date = LocalDate.parse(dateString.trim());
            // Convert to LocalDateTime at midnight (00:00:00)
            LocalDateTime dateTime = date.atStartOfDay();
            // Format as ISO datetime string
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            // If parsing fails, return the original string
            return dateString;
        }
    }
    
    private String inventoryToJson(BloodInventory inventory) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"bloodType\":\"").append(inventory.getBloodType()).append("\",");
        json.append("\"quantity\":").append(inventory.getQuantity()).append(",");
        json.append("\"unit\":\"").append(inventory.getUnit()).append("\",");
        json.append("\"status\":\"").append(inventory.getStatus()).append("\",");
        
        // Format the expiry date properly for the backend
        String formattedExpiryDate = formatDateForBackend(inventory.getExpiryDate());
        if (formattedExpiryDate != null) {
            json.append("\"expiryDate\":\"").append(formattedExpiryDate).append("\",");
        }
        
        json.append("\"batchNumber\":\"").append(inventory.getBatchNumber()).append("\"");
        json.append("}");
        return json.toString();
    }
    
    private Long extractLong(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
    
    private Integer extractInt(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
    
    private String extractString(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
