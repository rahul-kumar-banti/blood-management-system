package com.bloodbank.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private String authToken;

    public UserService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    // Get all users
    public List<User> getAllUsers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUsersFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to get users: " + response.statusCode() + " - " + response.body());
        }
    }

    // Get user by ID
    public User getUserById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUserFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to get user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Create new user
    public User createUser(User user) throws Exception {
        String userJson = userToJson(user);
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .uri(URI.create(BASE_URL + "/auth/register"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUserFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to create user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Update user
    public User updateUser(Long id, User user) throws Exception {
        String userJson = userToJson(user);
        
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUserFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to update user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Delete user
    public void deleteUser(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(BASE_URL + "/users/" + id))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 204) {
            throw new RuntimeException("Failed to delete user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Activate user
    public User activateUser(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(BASE_URL + "/users/" + id + "/activate"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUserFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to activate user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Deactivate user
    public User deactivateUser(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(BASE_URL + "/users/" + id + "/deactivate"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUserFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to deactivate user: " + response.statusCode() + " - " + response.body());
        }
    }

    // Search users
    public List<User> searchUsers(String name, String bloodType, String role, Boolean active) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/users/search?");
        if (name != null && !name.trim().isEmpty()) {
            urlBuilder.append("name=").append(name).append("&");
        }
        if (bloodType != null && !bloodType.trim().isEmpty()) {
            urlBuilder.append("bloodType=").append(bloodType).append("&");
        }
        if (role != null && !role.trim().isEmpty()) {
            urlBuilder.append("role=").append(role).append("&");
        }
        if (active != null) {
            urlBuilder.append("active=").append(active).append("&");
        }
        
        String url = urlBuilder.toString();
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseUsersFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to search users: " + response.statusCode() + " - " + response.body());
        }
    }

    // Get available roles
    public String[] getAvailableRoles() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users/roles"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseStringArrayFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to get roles: " + response.statusCode() + " - " + response.body());
        }
    }

    // Get available blood types
    public String[] getAvailableBloodTypes() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users/blood-types"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseStringArrayFromJson(response.body());
        } else {
            throw new RuntimeException("Failed to get blood types: " + response.statusCode() + " - " + response.body());
        }
    }

    // Validate username
    public boolean isUsernameAvailable(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users/validate-username/" + username))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseBooleanFromJson(response.body(), "available");
        } else {
            throw new RuntimeException("Failed to validate username: " + response.statusCode() + " - " + response.body());
        }
    }

    // Validate email
    public boolean isEmailAvailable(String email) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/users/validate-email/" + email))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return parseBooleanFromJson(response.body(), "available");
        } else {
            throw new RuntimeException("Failed to validate email: " + response.statusCode() + " - " + response.body());
        }
    }

    // Helper methods for JSON parsing
    private List<User> parseUsersFromJson(String json) {
        List<User> users = new ArrayList<>();
        // Simple JSON array parsing for users
        Pattern userPattern = Pattern.compile("\\{[^}]*\\}");
        Matcher matcher = userPattern.matcher(json);
        
        while (matcher.find()) {
            String userJson = matcher.group();
            try {
                User user = parseUserFromJson(userJson);
                users.add(user);
            } catch (Exception e) {
                // Skip invalid user entries
            }
        }
        return users;
    }

    private User parseUserFromJson(String json) {
        User user = new User();
        
        // Extract fields using regex patterns
        user.setId(extractLong(json, "id"));
        user.setUsername(extractString(json, "username"));
        user.setEmail(extractString(json, "email"));
        user.setFirstName(extractString(json, "firstName"));
        user.setLastName(extractString(json, "lastName"));
        user.setPhoneNumber(extractString(json, "phoneNumber"));
        user.setRole(extractString(json, "role"));
        user.setBloodType(extractString(json, "bloodType"));
        user.setActive(extractBoolean(json, "isActive"));
        user.setCreatedAt(extractString(json, "createdAt"));
        user.setUpdatedAt(extractString(json, "updatedAt"));
        
        return user;
    }

    private String[] parseStringArrayFromJson(String json) {
        // Extract array values from JSON
        List<String> values = new ArrayList<>();
        Pattern valuePattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = valuePattern.matcher(json);
        
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        
        return values.toArray(new String[0]);
    }

    private boolean parseBooleanFromJson(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }

    private Long extractLong(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private String extractString(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean extractBoolean(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }

    private String userToJson(User user) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"username\":\"").append(user.getUsername()).append("\",");
        json.append("\"email\":\"").append(user.getEmail()).append("\",");
        json.append("\"password\":\"").append(user.getPassword()).append("\",");
        json.append("\"firstName\":\"").append(user.getFirstName()).append("\",");
        json.append("\"lastName\":\"").append(user.getLastName()).append("\",");
        json.append("\"phoneNumber\":\"").append(user.getPhoneNumber()).append("\",");
        json.append("\"role\":\"").append(user.getRole()).append("\",");
        json.append("\"bloodType\":\"").append(user.getBloodType()).append("\"");
        json.append("}");
        return json.toString();
    }
}
