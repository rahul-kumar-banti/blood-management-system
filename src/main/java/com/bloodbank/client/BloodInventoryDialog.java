package com.bloodbank.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BloodInventoryDialog extends JDialog {
    private BloodInventory inventory;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    // Form fields
    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    private JComboBox<String> unitComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField expiryDateField;
    private JTextField batchNumberField;
    
    public BloodInventoryDialog(JFrame parent, BloodInventory inventory, boolean isEditMode) {
        super(parent, isEditMode ? "Edit Blood Inventory" : "Add New Blood Unit", true);
        this.inventory = inventory != null ? inventory : new BloodInventory();
        this.isEditMode = isEditMode;
        initComponents();
        loadInventoryData();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Blood Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        bloodTypeComboBox = new JComboBox<>(BloodTypeConverter.getDisplayValues());
        formPanel.add(bloodTypeComboBox, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(15);
        formPanel.add(quantityField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        unitComboBox = new JComboBox<>(new String[]{"ml", "units", "pints"});
        formPanel.add(unitComboBox, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"AVAILABLE", "RESERVED", "EXPIRED", "DISCARDED"});
        formPanel.add(statusComboBox, gbc);
        
        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        expiryDateField = new JTextField(15);
        formPanel.add(expiryDateField, gbc);
        
        // Batch Number
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Batch Number:"), gbc);
        gbc.gridx = 1;
        batchNumberField = new JTextField(15);
        formPanel.add(batchNumberField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveInventory());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void loadInventoryData() {
        if (isEditMode && inventory != null) {
            // Convert enum value to display value for the combo box
            String displayBloodType = BloodTypeConverter.convertToDisplay(inventory.getBloodType());
            bloodTypeComboBox.setSelectedItem(displayBloodType);
            quantityField.setText(String.valueOf(inventory.getQuantity()));
            unitComboBox.setSelectedItem(inventory.getUnit());
            statusComboBox.setSelectedItem(inventory.getStatus());
            expiryDateField.setText(inventory.getExpiryDate());
            batchNumberField.setText(inventory.getBatchNumber());
        } else {
            // Set defaults for new inventory
            expiryDateField.setText(LocalDate.now().plusDays(42).format(DateTimeFormatter.ISO_LOCAL_DATE));
            statusComboBox.setSelectedItem("AVAILABLE");
        }
    }
    
    private void saveInventory() {
        try {
            // Validate inputs
            if (quantityField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update inventory object - convert display blood type to enum value
            String selectedBloodType = (String) bloodTypeComboBox.getSelectedItem();
            String enumBloodType = BloodTypeConverter.convertToEnum(selectedBloodType);
            inventory.setBloodType(enumBloodType);
            inventory.setQuantity(quantity);
            inventory.setUnit((String) unitComboBox.getSelectedItem());
            inventory.setStatus((String) statusComboBox.getSelectedItem());
            inventory.setExpiryDate(expiryDateField.getText().trim());
            inventory.setBatchNumber(batchNumberField.getText().trim());
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public BloodInventory getInventory() {
        return inventory;
    }
}
