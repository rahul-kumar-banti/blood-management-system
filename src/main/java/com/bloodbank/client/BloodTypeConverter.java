package com.bloodbank.client;

/**
 * Utility class for converting between user-friendly blood type display values
 * and the enum values used by the backend.
 */
public class BloodTypeConverter {
    
    /**
     * Converts user-friendly blood type display to enum value
     */
    public static String convertToEnum(String displayValue) {
        if (displayValue == null) return null;
        
        switch (displayValue) {
            case "A+": return "A_POSITIVE";
            case "A-": return "A_NEGATIVE";
            case "B+": return "B_POSITIVE";
            case "B-": return "B_NEGATIVE";
            case "AB+": return "AB_POSITIVE";
            case "AB-": return "AB_NEGATIVE";
            case "O+": return "O_POSITIVE";
            case "O-": return "O_NEGATIVE";
            default: return displayValue; // fallback for already correct enum values
        }
    }
    
    /**
     * Converts enum value to user-friendly display
     */
    public static String convertToDisplay(String enumValue) {
        if (enumValue == null) return null;
        
        switch (enumValue) {
            case "A_POSITIVE": return "A+";
            case "A_NEGATIVE": return "A-";
            case "B_POSITIVE": return "B+";
            case "B_NEGATIVE": return "B-";
            case "AB_POSITIVE": return "AB+";
            case "AB_NEGATIVE": return "AB-";
            case "O_POSITIVE": return "O+";
            case "O_NEGATIVE": return "O-";
            default: return enumValue; // fallback for already correct display values
        }
    }
    
    /**
     * Gets the array of display values for combo boxes
     */
    public static String[] getDisplayValues() {
        return new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    }
    
    /**
     * Gets the array of enum values for combo boxes
     */
    public static String[] getEnumValues() {
        return new String[]{"A_POSITIVE", "A_NEGATIVE", "B_POSITIVE", "B_NEGATIVE", 
                           "AB_POSITIVE", "AB_NEGATIVE", "O_POSITIVE", "O_NEGATIVE"};
    }
}
