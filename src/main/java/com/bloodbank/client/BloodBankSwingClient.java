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
    private JPanel mainPanel; // New field for the main panel
    private JLabel welcomeLabel; // New field for the welcome label
    private String currentUsername; // New field for current username
    
    private HttpClient httpClient;
    
    // Login form components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // User management components
    private UserService userService;
    private JButton refreshButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton activateButton;
    private JButton deactivateButton;
    private UserTableModel userTableModel;
    private JTable userTable;
    private JScrollPane userScrollPane;
    
    // Blood Inventory components
    private BloodInventoryService bloodInventoryService;
    private BloodInventoryTableModel bloodInventoryTableModel;
    private JTable bloodInventoryTable;
    private JScrollPane bloodInventoryScrollPane;
    
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
    
    private void createMainTabs() {
        // Create top panel with welcome message and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        topPanel.setBackground(new Color(245, 245, 245));
        
        // Welcome label (will be updated when user logs in)
        JLabel welcomeLabel = new JLabel("Welcome!");
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
        
        // Store reference to welcome label for later updates
        this.welcomeLabel = welcomeLabel;
    }
    
    private void createInventoryPanel() {
        inventoryPanel = new JPanel(new BorderLayout());
        
        // Initialize blood inventory service
        bloodInventoryService = new BloodInventoryService();
        
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton addButton = new JButton("Add Blood Unit");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        
        // Create inventory table
        bloodInventoryTableModel = new BloodInventoryTableModel();
        bloodInventoryTable = new JTable(bloodInventoryTableModel);
        bloodInventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bloodInventoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        bloodInventoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        bloodInventoryTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Blood Type
        bloodInventoryTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Quantity
        bloodInventoryTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Unit
        bloodInventoryTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Status
        bloodInventoryTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Expiry Date
        bloodInventoryTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Batch Number
        bloodInventoryTable.getColumnModel().getColumn(7).setPreferredWidth(150);  // Created
        
        // Add table sorter
        TableRowSorter<BloodInventoryTableModel> sorter = new TableRowSorter<>(bloodInventoryTableModel);
        bloodInventoryTable.setRowSorter(sorter);
        
        // Create scroll pane
        bloodInventoryScrollPane = new JScrollPane(bloodInventoryTable);
        bloodInventoryScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Add double-click listener for editing
        bloodInventoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedInventory();
                }
            }
        });
        
        // Add action listeners
        refreshButton.addActionListener(e -> refreshInventory());
        addButton.addActionListener(e -> showAddInventoryDialog());
        editButton.addActionListener(e -> editSelectedInventory());
        deleteButton.addActionListener(e -> deleteSelectedInventory());
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Add selection listener to enable/disable buttons
        bloodInventoryTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = bloodInventoryTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
        
        inventoryPanel.add(controlPanel, BorderLayout.NORTH);
        inventoryPanel.add(bloodInventoryScrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshInventory();
    }
    
    private void createUsersPanel() {
        usersPanel = new JPanel(new BorderLayout());
        
        // Initialize user service
        userService = new UserService();
        
        // Create control panel with buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        refreshButton = new JButton("Refresh");
        addButton = new JButton("Add User");
        editButton = new JButton("Edit User");
        deleteButton = new JButton("Delete User");
        activateButton = new JButton("Activate");
        deactivateButton = new JButton("Deactivate");
        
        // Add buttons to control panel
        controlPanel.add(refreshButton);
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(activateButton);
        controlPanel.add(deactivateButton);
        
        // Create user table
        userTableModel = new UserTableModel();
        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Full Name
        userTable.getColumnModel().getColumn(3).setPreferredWidth(200);  // Email
        userTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Role
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Blood Type
        userTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Phone
        userTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Status
        userTable.getColumnModel().getColumn(8).setPreferredWidth(150);  // Created
        
        // Add table sorter
        TableRowSorter<UserTableModel> sorter = new TableRowSorter<>(userTableModel);
        userTable.setRowSorter(sorter);
        
        // Create scroll pane
        userScrollPane = new JScrollPane(userTable);
        userScrollPane.setPreferredSize(new Dimension(1000, 400));
        
        // Add double-click listener for editing
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedUser();
                }
            }
        });
        
        // Add action listeners
        refreshButton.addActionListener(e -> refreshUsers());
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        activateButton.addActionListener(e -> activateSelectedUser());
        deactivateButton.addActionListener(e -> deactivateSelectedUser());
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        activateButton.setEnabled(false);
        deactivateButton.setEnabled(false);
        
        // Add selection listener to enable/disable buttons
        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = userTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
            activateButton.setEnabled(hasSelection);
            deactivateButton.setEnabled(hasSelection);
        });
        
        // Add components to panel
        usersPanel.add(controlPanel, BorderLayout.NORTH);
        usersPanel.add(userScrollPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshUsers();
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
                currentUsername = username; // Store current username
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
    
    private void refreshInventory() {
        try {
            if (bloodInventoryService != null && authToken != null) {
                bloodInventoryService.setAuthToken(authToken);
                List<BloodInventory> inventory = bloodInventoryService.getAllInventory();
                bloodInventoryTableModel.setInventory(inventory);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error refreshing inventory: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddInventoryDialog() {
        if (bloodInventoryService != null && authToken != null) {
            bloodInventoryService.setAuthToken(authToken);
            BloodInventoryDialog dialog = new BloodInventoryDialog(this, null, false);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    BloodInventory inventory = dialog.getInventory();
                    bloodInventoryService.createInventory(inventory);
                    refreshInventory();
                    JOptionPane.showMessageDialog(this, "Blood inventory added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error saving inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please login first to add inventory.", "Authentication Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void editSelectedInventory() {
        int selectedRow = bloodInventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            BloodInventory inventory = bloodInventoryTableModel.getInventoryAt(selectedRow);
            if (bloodInventoryService != null && authToken != null) {
                bloodInventoryService.setAuthToken(authToken);
                BloodInventoryDialog dialog = new BloodInventoryDialog(this, inventory, true);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    try {
                        BloodInventory updatedInventory = dialog.getInventory();
                        bloodInventoryService.updateInventory(inventory.getId(), updatedInventory);
                        refreshInventory();
                        JOptionPane.showMessageDialog(this, "Blood inventory updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error updating inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedInventory() {
        int selectedRow = bloodInventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            BloodInventory inventory = bloodInventoryTableModel.getInventoryAt(selectedRow);
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this inventory item?", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (bloodInventoryService != null && authToken != null) {
                        bloodInventoryService.setAuthToken(authToken);
                        bloodInventoryService.deleteInventory(inventory.getId());
                        refreshInventory();
                        JOptionPane.showMessageDialog(this, "Blood inventory deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error deleting inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshUsers() {
        try {
            if (userService != null && authToken != null) {
                userService.setAuthToken(authToken);
                List<User> users = userService.getAllUsers();
                userTableModel.setUsers(users);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error refreshing users: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddUserDialog() {
        if (userService != null && authToken != null) {
            userService.setAuthToken(authToken);
            UserDialog dialog = new UserDialog(this, null, false, userService);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                refreshUsers();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please login first to add users.", 
                    "Authentication Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            User user = userTableModel.getUserAt(selectedRow);
            if (userService != null && authToken != null) {
                userService.setAuthToken(authToken);
                UserDialog dialog = new UserDialog(this, user, true, userService);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    refreshUsers();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            User user = userTableModel.getUserAt(selectedRow);
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + user.getUsername() + "?", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (userService != null && authToken != null) {
                        userService.setAuthToken(authToken);
                        userService.deleteUser(user.getId());
                        refreshUsers();
                        JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void activateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            User user = userTableModel.getUserAt(selectedRow);
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to activate user " + user.getUsername() + "?", 
                    "Confirm Activation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (userService != null && authToken != null) {
                        userService.setAuthToken(authToken);
                        userService.activateUser(user.getId());
                        refreshUsers();
                        JOptionPane.showMessageDialog(this, "User activated successfully!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error activating user: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to activate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deactivateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            User user = userTableModel.getUserAt(selectedRow);
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to deactivate user " + user.getUsername() + "?", 
                    "Confirm Deactivation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (userService != null && authToken != null) {
                        userService.setAuthToken(authToken);
                        userService.deactivateUser(user.getId());
                        refreshUsers();
                        JOptionPane.showMessageDialog(this, "User deactivated successfully!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error deactivating user: " + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to deactivate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshDonations() {
        JOptionPane.showMessageDialog(this, "Refreshing donations...");
    }
    
    private void showAddDonationDialog() {
        DonationDialog dialog = new DonationDialog(this, null, false);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            // TODO: Save donation to backend
            JOptionPane.showMessageDialog(this, "Blood donation added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshRequests() {
        JOptionPane.showMessageDialog(this, "Refreshing requests...");
    }
    
    private void showAddRequestDialog() {
        RequestDialog dialog = new RequestDialog(this, null, false);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            // TODO: Save request to backend
            JOptionPane.showMessageDialog(this, "Blood request added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // New method to clear login form
    private void clearLoginForm() {
        usernameField.setText("");
        passwordField.setText("");
    }

    // New method to clear current username
    private void clearCurrentUsername() {
        currentUsername = null;
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankSwingClient());
    }
}
