/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author jacky
 */
public class CancelRescheduleFrame extends JFrame {
    private JComboBox<String> appointmentDropdown;
    private Map<String, String> appointmentMap = new HashMap<>(); // To map display strings to original data

    public CancelRescheduleFrame(String studentUsername) {
        setTitle("Cancel/Reschedule Appointment - " + studentUsername);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

        // Components
        JLabel lblSelectAppointment = new JLabel("Select Appointment:");
        lblSelectAppointment.setForeground(Color.WHITE); // Set text color to white
        appointmentDropdown = new JComboBox<>();
        JButton btnCancel = new JButton("Cancel Appointment");
        JButton btnReschedule = new JButton("Reschedule Appointment");
        JButton btnClose = new JButton("Close");

        // Load student's appointments
        loadAppointments(studentUsername);

        // Cancel Appointment
        btnCancel.addActionListener(e -> {
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();
            if (selectedAppointment != null) {
                cancelAppointment(studentUsername, selectedAppointment);
                loadAppointments(studentUsername); // Refresh dropdown
                JOptionPane.showMessageDialog(this, "Appointment canceled successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No appointment selected.");
            }
        });

        // Reschedule Appointment
        btnReschedule.addActionListener(e -> {
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();
            if (selectedAppointment != null) {
                String originalData = appointmentMap.get(selectedAppointment);
                dispose();
                new RescheduleAppointmentFrame(studentUsername, originalData); // Redirect to reschedule frame
            } else {
                JOptionPane.showMessageDialog(this, "No appointment selected.");
            }
        });

        // Close Frame
        btnClose.addActionListener(e -> dispose());

        // Layout
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(lblSelectAppointment);
        panel.add(appointmentDropdown);
        panel.add(btnCancel);
        panel.add(btnReschedule);
        panelBackground.add(panel, BorderLayout.CENTER);
        panelBackground.add(btnClose, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loadAppointments(String studentUsername) {
        // Clear previous data
        appointmentDropdown.removeAllItems();
        appointmentMap.clear();

        File file = new File("resources/appointments.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Appointments file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> appointments = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                // Split the line into parts and ensure it has enough fields
                String[] parts = line.split(",", -1); // Use -1 to keep empty fields
                if (parts.length >= 5) {
                    String student = parts[0].trim();
                    String lecturer = parts[1].trim();
                    String slot = parts[3].trim();
                    String status = parts[4].trim();

                    // Check if the record belongs to the current student and is pending
                    if (student.equals(studentUsername) && status.equalsIgnoreCase("Pending")) {
                        if (!slot.isEmpty()) {
                            appointments.add(new String[]{lecturer, slot, line});
                        } else {
                            System.err.println("Invalid or missing slot for line: " + line);
                        }
                    }
                } else {
                    System.err.println("Skipping invalid line: " + line);
                }
            }

            // Sort appointments by Lecturer and then by Date/Time
            appointments.sort((a, b) -> {
                int lecturerComparison = a[0].compareTo(b[0]);
                if (lecturerComparison == 0) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date dateA = sdf.parse(a[1]);
                        Date dateB = sdf.parse(b[1]);
                        return dateA.compareTo(dateB);
                    } catch (Exception e) {
                        System.err.println("Error parsing date for comparison.");
                        e.printStackTrace();
                    }
                }
                return lecturerComparison;
            });

            // Populate dropdown and map
            for (String[] appointment : appointments) {
                String display = String.format("Lecturer: %s, Date: %s, Time: %s",
                        appointment[0], appointment[1].substring(0, 10), appointment[1].substring(11));
                appointmentMap.put(display, appointment[2]); // Map display string to original data
                appointmentDropdown.addItem(display); // Add the display string to the dropdown
            }

            // If no appointments are found, notify the user
            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending appointments found.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments.");
            e.printStackTrace();
        }
    }

    private void cancelAppointment(String studentUsername, String selectedAppointment) {
        File inputFile = new File("resources/appointments.txt");
        File tempFile = new File("resources/temp_appointments.txt");

        String originalAppointment = appointmentMap.get(selectedAppointment); // Get the original data

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(originalAppointment)) { // Skip the canceled appointment
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error canceling appointment.");
            e.printStackTrace();
        }

        // Replace original file with updated file
        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            System.out.println("Appointment canceled.");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating appointment file.");
        }
    }
}
