package com.bloodbank.client;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RequestDialog extends JDialog {
    private Request request;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    // Form fields
    private JTextField hospitalField;
    private JTextField patientField;
    private JComboBox<String> bloodTypeComboBox;
    private JTextField quantityField;
    private JComboBox<String> unitComboBox;
    private JComboBox<String> priorityComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField requestDateField;
    private JTextArea notesArea;
    
    public RequestDialog(JFrame parent, Request request, boolean isEditMode) {
        super(parent, isEditMode ? "Edit Request" : "Add New Request", true);
        this.request = request != null ? request : new Request();
        this.isEditMode = isEditMode;
        initComponents();
        loadRequestData();
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
        
        // Hospital
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Hospital:"), gbc);
        gbc.gridx = 1;
        hospitalField = new JTextField(15);
        formPanel.add(hospitalField, gbc);
        
        // Patient
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientField = new JTextField(15);
        formPanel.add(patientField, gbc);
        
        // Blood Type
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        bloodTypeComboBox = new JComboBox<>(BloodTypeConverter.getDisplayValues());
        formPanel.add(bloodTypeComboBox, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField = new JTextField(15);
        formPanel.add(quantityField, gbc);
        
        // Unit
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Unit:"), gbc);
        gbc.gridx = 1;
        unitComboBox = new JComboBox<>(new String[]{"ml", "units", "pints"});
        formPanel.add(unitComboBox, gbc);
        
        // Priority
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        priorityComboBox = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"});
        formPanel.add(priorityComboBox, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"PENDING", "APPROVED", "FULFILLED", "CANCELLED", "REJECTED"});
        formPanel.add(statusComboBox, gbc);
        
        // Request Date
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Request Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        requestDateField = new JTextField(15);
        formPanel.add(requestDateField, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 8;
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
        
        saveButton.addActionListener(e -> saveRequest());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void loadRequestData() {
        if (isEditMode && request != null) {
            hospitalField.setText(request.getHospital());
            patientField.setText(request.getPatient());
            // Convert enum value to display value for the combo box
            String displayBloodType = BloodTypeConverter.convertToDisplay(request.getBloodType());
            bloodTypeComboBox.setSelectedItem(displayBloodType);
            quantityField.setText(String.valueOf(request.getQuantity()));
            unitComboBox.setSelectedItem(request.getUnit());
            priorityComboBox.setSelectedItem(request.getPriority());
            statusComboBox.setSelectedItem(request.getStatus());
            requestDateField.setText(request.getRequestDate());
            notesArea.setText(request.getNotes());
        } else {
            // Set defaults for new request
            requestDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            statusComboBox.setSelectedItem("PENDING");
            priorityComboBox.setSelectedItem("MEDIUM");
        }
    }
    
    private void saveRequest() {
        try {
            // Validate inputs
            if (hospitalField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter hospital name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (patientField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter patient name", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            
            // Update request object
            request.setHospital(hospitalField.getText().trim());
            request.setPatient(patientField.getText().trim());
            // Convert display blood type to enum value
            String selectedBloodType = (String) bloodTypeComboBox.getSelectedItem();
            String enumBloodType = BloodTypeConverter.convertToEnum(selectedBloodType);
            request.setBloodType(enumBloodType);
            request.setQuantity(quantity);
            request.setUnit((String) unitComboBox.getSelectedItem());
            request.setPriority((String) priorityComboBox.getSelectedItem());
            request.setStatus((String) statusComboBox.getSelectedItem());
            request.setRequestDate(requestDateField.getText().trim());
            request.setNotes(notesArea.getText().trim());
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Request getRequest() {
        return request;
    }
}
