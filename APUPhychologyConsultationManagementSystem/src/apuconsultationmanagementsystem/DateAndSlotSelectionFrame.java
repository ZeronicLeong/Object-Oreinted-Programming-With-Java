/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author jacky
 */
public class DateAndSlotSelectionFrame extends JFrame {
    private String studentUsername;
    private String lecturerUsername;

    public DateAndSlotSelectionFrame(String studentUsername, String lecturerUsername) {
        this.studentUsername = studentUsername;
        this.lecturerUsername = lecturerUsername;
        
        setTitle("Select Date and Slot - " + lecturerUsername);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create a custom JPanel with background image
        JPanel panelBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("resources/background.jpeg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelBackground.setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Select Date and Time Slot", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE); // White text color
        add(lblTitle, BorderLayout.NORTH);

        // Center Panel with Date and Time Slot selection
        JPanel panelCenter = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCenter.setOpaque(false); // Set transparent background for the center panel

        JLabel lblDate = new JLabel("Select Date:");
        lblDate.setForeground(Color.WHITE);  // White text color
        JDateChooser dateChooser = Utils.createDateChooser();

        JLabel lblTimeSlot = new JLabel("Select Time Slot:");
        lblTimeSlot.setForeground(Color.WHITE); // White text color
        JComboBox<String> cbTimeSlots = new JComboBox<>();

        JButton btnLoadSlots = new JButton("Load Available Slots");
        Utils.setRoundedButton(btnLoadSlots);

        panelCenter.add(lblDate);
        panelCenter.add(dateChooser);
        panelCenter.add(lblTimeSlot);
        panelCenter.add(cbTimeSlots);
        panelCenter.add(new JLabel()); // Empty cell
        panelCenter.add(btnLoadSlots);

        panelBackground.add(panelCenter, BorderLayout.CENTER);

        // Book Appointment Button
        JButton btnBook = new JButton("Book Appointment");
        Utils.setRoundedButton(btnBook);

        btnBook.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            String selectedSlot = (String) cbTimeSlots.getSelectedItem();

            if (selectedDate == null || selectedSlot == null) {
                JOptionPane.showMessageDialog(this, "Please select a date and time slot.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(selectedDate);

            bookAppointment(dateStr, selectedSlot);
        });

        // Add the Book Appointment button to the bottom of the frame
        panelBackground.add(btnBook, BorderLayout.SOUTH);

        // Load Available Slots Button Action Listener
        btnLoadSlots.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Please select a date first.");
                return;
            }
            loadAvailableSlots(selectedDate, cbTimeSlots);
        });

        add(panelBackground, BorderLayout.CENTER);
        setVisible(true);
    }

    // Load available slots for a given date
    private void loadAvailableSlots(Date date, JComboBox<String> cbTimeSlots) {
        cbTimeSlots.removeAllItems();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        ArrayList<String> availableSlots = new ArrayList<>();

        // Load lecturer's slots from lecturers_slots.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/lecturers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(lecturerUsername)) {
                    String slotDateTime = parts[1]; // Format: yyyy-MM-dd HH:mm
                    if (slotDateTime.startsWith(dateStr)) {
                        // Check if the slot is already booked
                        if (!isSlotBooked(lecturerUsername, slotDateTime)) {
                            String time = slotDateTime.substring(11); // Extract time part
                            availableSlots.add(time);
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading slots.");
            e.printStackTrace();
        }

        if (availableSlots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available slots on this date.");
        } else {
            for (String slot : availableSlots) {
                cbTimeSlots.addItem(slot);
            }
        }
    }

    // Check if the slot is already booked
    private boolean isSlotBooked(String lecturerUsername, String slotDateTime) {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/appointments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(lecturerUsername) && parts[3].equals(slotDateTime)) {
                    if (parts[4].equals("Pending") || parts[4].equals("Approved")) {
                        return true; // Slot is already booked
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking booked slots.");
            e.printStackTrace();
        }
        return false;
    }

    // Book an appointment by saving the details in the appointments.txt file
    private void bookAppointment(String dateStr, String timeStr) {
        String slotDateTime = dateStr + " " + timeStr;
        String status = "Pending";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/appointments.txt", true))) {
            writer.write(studentUsername + "," + lecturerUsername + "," + dateStr + "," + slotDateTime + "," + status);
            writer.newLine();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully! Awaiting lecturer approval.");
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error booking appointment.");
            e.printStackTrace();
        }
    }
}
