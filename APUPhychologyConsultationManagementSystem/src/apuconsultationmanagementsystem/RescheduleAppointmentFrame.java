/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
/**
 *
 * @author jacky
 */
public class RescheduleAppointmentFrame extends JFrame {
    private JComboBox<String> lecturerDropdown;
    private JComboBox<String> slotDropdown;
    private String originalAppointment;

    public RescheduleAppointmentFrame(String studentUsername, String originalAppointment) {
        this.originalAppointment = originalAppointment;

        setTitle("Reschedule Appointment - " + studentUsername);
        setSize(600, 500);
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

        JLabel lblLecturer = new JLabel("Select Lecturer:");
        lblLecturer.setForeground(Color.WHITE);
        lecturerDropdown = new JComboBox<>();
        loadLecturers();

        JLabel lblOldSlot = new JLabel("Current Appointment:");
        lblOldSlot.setForeground(Color.WHITE);
        JTextField txtOldSlot = new JTextField(originalAppointment);
        txtOldSlot.setEditable(false);

        JLabel lblNewSlot = new JLabel("Select New Slot:");
        lblNewSlot.setForeground(Color.WHITE);
        slotDropdown = new JComboBox<>();

        JButton btnLoadSlots = new JButton("Load Slots");
        JButton btnOk = new JButton("OK");
        JButton btnClose = new JButton("Close");

        // Set button styles
        setButtonStyle(btnLoadSlots);
        setButtonStyle(btnOk);
        setButtonStyle(btnClose);

        btnLoadSlots.addActionListener(e -> loadAvailableSlots());

        btnOk.addActionListener(e -> {
            String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
            String selectedSlot = (String) slotDropdown.getSelectedItem();

            if (selectedLecturer == null || selectedSlot == null) {
                JOptionPane.showMessageDialog(this, "Please select a lecturer and a new slot.");
                return;
            }

            saveRescheduleRequest(studentUsername, selectedLecturer, originalAppointment, selectedSlot);
            JOptionPane.showMessageDialog(this, "Reschedule request submitted successfully.");
            dispose(); // Close the frame
        });

        btnClose.addActionListener(e -> dispose());

        JPanel panelCenter = new JPanel(new GridLayout(6, 1, 10, 10));
        panelCenter.setOpaque(false);
        panelCenter.add(lblLecturer);
        panelCenter.add(lecturerDropdown);
        panelCenter.add(lblOldSlot);
        panelCenter.add(txtOldSlot);
        panelCenter.add(lblNewSlot);
        panelCenter.add(slotDropdown);
        panelCenter.add(btnLoadSlots);

        panelBackground.add(panelCenter, BorderLayout.CENTER);

        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelSouth.setOpaque(false);
        panelSouth.add(btnOk);
        panelSouth.add(btnClose);

        btnOk.setPreferredSize(new Dimension(100, 40));
        btnClose.setPreferredSize(new Dimension(100, 40));

        panelBackground.add(panelSouth, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);
        setVisible(true);
    }

    private void setButtonStyle(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(51, 51, 51)); // Light black background
        button.setForeground(Color.WHITE);          // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100))); // Slightly lighter border
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadLecturers() {
        lecturerDropdown.removeAllItems();
        File file = new File("resources/lecturers_slots.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Lecturers file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Set<String> uniqueLecturers = new HashSet<>(); // To track added lecturers
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    String lecturer = parts[0].trim();
                    if (!uniqueLecturers.contains(lecturer)) {
                        uniqueLecturers.add(lecturer);
                        lecturerDropdown.addItem(lecturer); // Add unique lecturer name to dropdown
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading lecturers.");
        }
    }

    private void loadAvailableSlots() {
        slotDropdown.removeAllItems();
        String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
        if (selectedLecturer == null) {
            JOptionPane.showMessageDialog(this, "Please select a lecturer.");
            return;
        }

        File file = new File("resources/lecturers_slots.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Lecturers slots file not found.");
            return;
        }

        List<String> availableSlots = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(selectedLecturer)) {
                    availableSlots.add(parts[1]); // Add slot times to the list
                }
            }

            // Sort slots by date and time
            availableSlots.sort((slot1, slot2) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    Date date1 = sdf.parse(slot1);
                    Date date2 = sdf.parse(slot2);
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });

            // Populate dropdown with sorted slots
            for (String slot : availableSlots) {
                slotDropdown.addItem(slot);
            }

            if (availableSlots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available slots found for the selected lecturer.");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading available slots.");
            e.printStackTrace();
        }
    }

    private void saveRescheduleRequest(String studentUsername, String lecturer, String oldSlot, String newSlot) {
        File rescheduleFile = new File("resources/reschedule_requests.txt");
        File appointmentsFile = new File("resources/appointments.txt");
        File tempAppointmentsFile = new File("resources/temp_appointments.txt");

        boolean appointmentFound = false;

        // Step 1: Remove the old appointment from appointments.txt
        try (
            BufferedReader reader = new BufferedReader(new FileReader(appointmentsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempAppointmentsFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Keep your original comparison logic intact
                if (line.trim().equals(originalAppointment.trim())) {
                    appointmentFound = true;
                    System.out.println("Appointment Found and Removed: " + line.trim()); // Debugging
                    continue; // Skip the matching appointment (mark it as "removed")
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing appointments file.");
            e.printStackTrace();
            return;
        }

        // Replace the old appointments file with the updated one
        if (appointmentFound) {
            if (!appointmentsFile.delete() || !tempAppointmentsFile.renameTo(appointmentsFile)) {
                JOptionPane.showMessageDialog(this, "Error updating appointments file.");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Appointment not found. Reschedule failed.");
            return;
        }

        // Step 2: Save the reschedule request in reschedule_requests.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rescheduleFile, true))) {
            // Extract valid data from `originalAppointment`
            String[] appointmentParts = originalAppointment.split(",", -1);

            // Ensure `appointmentParts` contains enough fields
            if (appointmentParts.length >= 5) {
                String oldStudent = appointmentParts[0].trim(); // Old student name
                String oldLecturer = appointmentParts[1].trim(); // Old lecturer name
                oldSlot = appointmentParts[3].trim(); // Old slot

                // Save the reschedule request in the correct format
                String formattedRequest = String.format("%s,%s,%s,%s,Pending",
                        oldStudent, lecturer, oldSlot, newSlot);
                writer.write(formattedRequest);
                writer.newLine();

                // Debugging Output
                System.out.println("Reschedule Request Saved: " + formattedRequest);
            } else {
                System.err.println("Invalid original appointment format: " + originalAppointment);
                JOptionPane.showMessageDialog(this, "Error: Invalid original appointment format.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving reschedule request.");
            e.printStackTrace();
        }
    }
}