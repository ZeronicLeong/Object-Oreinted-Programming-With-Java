/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.util.Date;
import javax.swing.*;
/**
 *
 * @author jacky
 */
public class Utils {
    public static void setRoundedButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }

    public static JDateChooser createDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        return dateChooser;
    }

    static String formatDate(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
