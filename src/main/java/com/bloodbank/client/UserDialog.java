package com.bloodbank.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDialog extends JDialog {
    private User user;
    private boolean isEditMode;
    private UserService userService;
    
    // Form fields
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneNumberField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> bloodTypeComboBox;
    private JCheckBox activeCheckBox;
    
    // Buttons
    private JButton saveButton;
    private JButton cancelButton;
    
    // Result
    private boolean confirmed = false;

    public UserDialog(JFrame parent, User user, boolean isEditMode, UserService userService) {
        super(parent, isEditMode ? "Edit User" : "Add New User", true);
        this.user = user != null ? user : new User();
        this.isEditMode = isEditMode;
        this.userService = userService;
        
        initComponents();
        loadUserData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set dialog properties
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        if (isEditMode) {
            passwordField.setEnabled(false);
            passwordField.setText("********");
        }
        panel.add(passwordField, gbc);
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        panel.add(firstNameField, gbc);
        
        // Last Name
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        panel.add(lastNameField, gbc);
        
        // Phone Number
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneNumberField = new JTextField(20);
        panel.add(phoneNumberField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"ADMIN", "DOCTOR", "NURSE", "TECHNICIAN", "DONOR", "RECIPIENT"});
        panel.add(roleComboBox, gbc);
        
        // Blood Type
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        bloodTypeComboBox = new JComboBox<>(new String[]{"A_POSITIVE", "A_NEGATIVE", "B_POSITIVE", "B_NEGATIVE", 
                                                         "AB_POSITIVE", "AB_NEGATIVE", "O_POSITIVE", "O_NEGATIVE"});
        panel.add(bloodTypeComboBox, gbc);
        
        // Active Status
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setSelected(true);
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    saveUser();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }

    private void loadUserData() {
        if (isEditMode && user != null) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            phoneNumberField.setText(user.getPhoneNumber());
            
            if (user.getRole() != null) {
                roleComboBox.setSelectedItem(user.getRole());
            }
            
            if (user.getBloodType() != null) {
                bloodTypeComboBox.setSelectedItem(user.getBloodType());
            }
            
            activeCheckBox.setSelected(user.isActive());
        }
    }

    private boolean validateForm() {
        // Basic validation
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        if (!isEditMode && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return false;
        }
        
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void saveUser() {
        try {
            // Update user object with form data
            user.setUsername(usernameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setFirstName(firstNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setPhoneNumber(phoneNumberField.getText().trim());
            user.setRole((String) roleComboBox.getSelectedItem());
            user.setBloodType((String) bloodTypeComboBox.getSelectedItem());
            user.setActive(activeCheckBox.isSelected());
            
            if (!isEditMode) {
                user.setPassword(new String(passwordField.getPassword()));
            }
            
            // Save user
            if (isEditMode) {
                userService.updateUser(user.getId(), user);
                JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                userService.createUser(user);
                JOptionPane.showMessageDialog(this, "User created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + ex.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public User getUser() {
        return user;
    }
}
