package com.bloodbank.client;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private List<User> users = new ArrayList<>();
    private final String[] columnNames = {
        "ID", "Username", "Full Name", "Email", "Role", "Blood Type", "Phone", "Status", "Created"
    };

    public UserTableModel() {}

    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
        fireTableDataChanged();
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUserAt(int row) {
        if (row >= 0 && row < users.size()) {
            return users.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return users.size();
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
        if (rowIndex >= users.size()) {
            return null;
        }

        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0: return user.getId();
            case 1: return user.getUsername();
            case 2: return user.getFullName();
            case 3: return user.getEmail();
            case 4: return user.getRole();
            case 5: return user.getBloodType();
            case 6: return user.getPhoneNumber();
            case 7: return user.getStatusText();
            case 8: return user.getCreatedAt();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;
            case 7: return String.class;
            default: return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Read-only table
    }

    public void addUser(User user) {
        users.add(user);
        fireTableRowsInserted(users.size() - 1, users.size() - 1);
    }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                fireTableRowsUpdated(i, i);
                break;
            }
        }
    }

    public void removeUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.remove(i);
                fireTableRowsDeleted(i, i);
                break;
            }
        }
    }

    public void clearUsers() {
        users.clear();
        fireTableDataChanged();
    }
}
