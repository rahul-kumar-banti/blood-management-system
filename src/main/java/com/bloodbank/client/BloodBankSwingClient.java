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

public class BloodBankSwingClient extends JFrame {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private String authToken;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel;
    private JPanel inventoryPanel;
    private JPanel usersPanel;
    private JPanel donationsPanel;
    private JPanel requestsPanel;
    
    private HttpClient httpClient;
    
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
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
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
    
    private void createMainTabs() {
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
    }
    
    private void createInventoryPanel() {
        inventoryPanel = new JPanel(new BorderLayout());
        
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Blood Unit");
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        
        // Table for inventory
        String[] columns = {"ID", "Blood Type", "Quantity", "Unit", "Status", "Expiry Date", "Batch Number"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        inventoryPanel.add(controlPanel, BorderLayout.NORTH);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);
        
        refreshButton.addActionListener(e -> refreshInventory());
        addButton.addActionListener(e -> showAddInventoryDialog());
    }
    
    private void createUsersPanel() {
        usersPanel = new JPanel(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Users");
        JButton addButton = new JButton("Add User");
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        
        String[] columns = {"ID", "Username", "Name", "Role", "Blood Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        usersPanel.add(controlPanel, BorderLayout.NORTH);
        usersPanel.add(scrollPane, BorderLayout.CENTER);
        
        refreshButton.addActionListener(e -> refreshUsers());
        addButton.addActionListener(e -> showAddUserDialog());
    }
    
    private void createDonationsPanel() {
        donationsPanel = new JPanel(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Donations");
        JButton addButton = new JButton("Add Donation");
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        
        String[] columns = {"ID", "Donor", "Blood Type", "Quantity", "Status", "Donation Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        donationsPanel.add(controlPanel, BorderLayout.NORTH);
        donationsPanel.add(scrollPane, BorderLayout.CENTER);
        
        refreshButton.addActionListener(e -> refreshDonations());
        addButton.addActionListener(e -> showAddDonationDialog());
    }
    
    private void createRequestsPanel() {
        requestsPanel = new JPanel(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Requests");
        JButton addButton = new JButton("Add Request");
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        
        String[] columns = {"ID", "Hospital", "Patient", "Blood Type", "Quantity", "Priority", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        requestsPanel.add(controlPanel, BorderLayout.NORTH);
        requestsPanel.add(scrollPane, BorderLayout.CENTER);
        
        refreshButton.addActionListener(e -> refreshRequests());
        addButton.addActionListener(e -> showAddRequestDialog());
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
                SwingUtilities.invokeLater(() -> {
                    setContentPane(tabbedPane);
                    revalidate();
                    repaint();
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
    
    private void refreshInventory() {
        // Implementation for refreshing inventory data
        JOptionPane.showMessageDialog(this, "Refreshing inventory...");
    }
    
    private void showAddInventoryDialog() {
        JOptionPane.showMessageDialog(this, "Add inventory dialog would appear here");
    }
    
    private void refreshUsers() {
        JOptionPane.showMessageDialog(this, "Refreshing users...");
    }
    
    private void showAddUserDialog() {
        JOptionPane.showMessageDialog(this, "Add user dialog would appear here");
    }
    
    private void refreshDonations() {
        JOptionPane.showMessageDialog(this, "Refreshing donations...");
    }
    
    private void showAddDonationDialog() {
        JOptionPane.showMessageDialog(this, "Add donation dialog would appear here");
    }
    
    private void refreshRequests() {
        JOptionPane.showMessageDialog(this, "Refreshing requests...");
    }
    
    private void showAddRequestDialog() {
        JOptionPane.showMessageDialog(this, "Add request dialog would appear here");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankSwingClient());
    }
}
