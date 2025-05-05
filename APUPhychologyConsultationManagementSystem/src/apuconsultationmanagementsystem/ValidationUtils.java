/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author jacky
 */
public class ValidationUtils {

    // Validate date format (e.g., "2024-12-07 10:00")
    public static boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false); // Strictly enforce date parsing
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String slot) {
    try {
        // Check if the slot is in valid format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse(slot);
        
        // Get hour part of the time
        int hour = date.getHours();
        
        // Ensure the time is between 7 AM and 6 PM
        return (hour >= 7 && hour <= 18);
    } catch (ParseException e) {
        return false; // Return false if parsing fails
    }
}
}
