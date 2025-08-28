package com.bloodbank.client;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DonationDialog extends JDialog {
    private Donation donation;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    // Form fields
    private JComboBox<String> donorComboBox;
    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    private JComboBox<String> unitComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField donationDateField;
    private JTextArea notesArea;
    
    public DonationDialog(JFrame parent, Donation donation, boolean isEditMode) {
        super(parent, isEditMode ? "Edit Donation" : "Add New Donation", true);
        this.donation = donation != null ? donation : new Donation();
        this.isEditMode = isEditMode;
        initComponents();
        loadDonationData();
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
        
        // Donor
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Donor:"), gbc);
        gbc.gridx = 1;
        donorComboBox = new JComboBox<>(new String[]{"Select Donor", "John Doe", "Jane Smith", "Bob Johnson"});
        formPanel.add(donorComboBox, gbc);
        
        // Blood Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        bloodTypeComboBox = new JComboBox<>(BloodTypeConverter.getDisplayValues());
        formPanel.add(bloodTypeComboBox, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(15);
        formPanel.add(quantityField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        unitComboBox = new JComboBox<>(new String[]{"ml", "units", "pints"});
        formPanel.add(unitComboBox, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "COMPLETED", "CANCELLED", "REJECTED"});
        formPanel.add(statusComboBox, gbc);
        
        // Donation Date
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Donation Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        donationDateField = new JTextField(15);
        formPanel.add(donationDateField, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 15);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        formPanel.add(notesScrollPane, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveDonation());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void loadDonationData() {
        if (isEditMode && donation != null) {
            donorComboBox.setSelectedItem(donation.getDonor());
            // Convert enum value to display value for the combo box
            String displayBloodType = BloodTypeConverter.convertToDisplay(donation.getBloodType());
            bloodTypeComboBox.setSelectedItem(displayBloodType);
            quantityField.setText(String.valueOf(donation.getQuantity()));
            unitComboBox.setSelectedItem(donation.getUnit());
            statusComboBox.setSelectedItem(donation.getStatus());
            donationDateField.setText(donation.getDonationDate());
            notesArea.setText(donation.getNotes());
        } else {
            // Set defaults for new donation
            donationDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            statusComboBox.setSelectedItem("PENDING");
        }
    }
    
    private void saveDonation() {
        try {
            // Validate inputs
            if (donorComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a donor", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (quantityField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update donation object
            donation.setDonor((String) donorComboBox.getSelectedItem());
            // Convert display blood type to enum value
            String selectedBloodType = (String) bloodTypeComboBox.getSelectedItem();
            String enumBloodType = BloodTypeConverter.convertToEnum(selectedBloodType);
            donation.setBloodType(enumBloodType);
            donation.setQuantity(quantity);
            donation.setUnit((String) unitComboBox.getSelectedItem());
            donation.setStatus((String) statusComboBox.getSelectedItem());
            donation.setDonationDate(donationDateField.getText().trim());
            donation.setNotes(notesArea.getText().trim());
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Donation getDonation() {
        return donation;
    }
}
