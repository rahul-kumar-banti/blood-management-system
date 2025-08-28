package com.bloodbank.client;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BloodInventoryTableModel extends AbstractTableModel {
    private List<BloodInventory> inventory = new ArrayList<>();
    private final String[] columnNames = {
        "ID", "Blood Type", "Quantity", "Unit", "Status", "Expiry Date", "Batch Number", "Created"
    };
    
    public void setInventory(List<BloodInventory> inventory) {
        this.inventory = inventory;
        fireTableDataChanged();
    }
    
    public void addInventory(BloodInventory item) {
        this.inventory.add(item);
        fireTableRowsInserted(this.inventory.size() - 1, this.inventory.size() - 1);
    }
    
    public void updateInventory(int row, BloodInventory item) {
        if (row >= 0 && row < this.inventory.size()) {
            this.inventory.set(row, item);
            fireTableRowsUpdated(row, row);
        }
    }
    
    public void removeInventory(int row) {
        if (row >= 0 && row < this.inventory.size()) {
            this.inventory.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public BloodInventory getInventoryAt(int row) {
        if (row >= 0 && row < this.inventory.size()) {
            return this.inventory.get(row);
        }
        return null;
    }
    
    @Override
    public int getRowCount() {
        return inventory.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= inventory.size()) {
            return null;
        }
        
        BloodInventory item = inventory.get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getId();
            case 1: return item.getBloodType();
            case 2: return item.getQuantity();
            case 3: return item.getUnit();
            case 4: return item.getStatus();
            case 5: return item.getExpiryDate();
            case 6: return item.getBatchNumber();
            case 7: return item.getCreatedAt();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Make table read-only
    }
}
