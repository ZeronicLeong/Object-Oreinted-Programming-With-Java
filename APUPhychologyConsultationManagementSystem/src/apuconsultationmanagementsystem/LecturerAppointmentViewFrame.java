/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *
 * @author jacky
 */
public class LecturerAppointmentViewFrame extends JFrame {

    public LecturerAppointmentViewFrame(String lecturerUsername) {
        setTitle("View Student Appointments - " + lecturerUsername);
        setSize(600, 400);
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

        // Title Label
        JLabel lblAppointments = new JLabel("Appointments:", JLabel.CENTER);
        lblAppointments.setFont(new Font("Arial", Font.BOLD, 18));
        lblAppointments.setForeground(Color.WHITE);  // White text color
        panelBackground.add(lblAppointments, BorderLayout.NORTH);

        // Components
        JComboBox<String> appointmentsDropdown = new JComboBox<>();
        JButton btnApprove = new JButton("Approve Appointment");
        JButton btnReject = new JButton("Reject Appointment");
        JButton btnViewAll = new JButton("View All Appointments");
        JButton btnClose = new JButton("Close");

        // Set button appearance for Approve, Reject, and Close buttons
        setButtonStyle(btnApprove);
        setButtonStyle(btnReject);
        setButtonStyle(btnViewAll);
        setButtonStyle(btnClose);

        // Load pending appointments into the dropdown
        loadPendingAppointments(lecturerUsername, appointmentsDropdown);

        // Approve appointment
        btnApprove.addActionListener(e -> {
            String selectedAppointment = (String) appointmentsDropdown.getSelectedItem();
            if (selectedAppointment != null) {
                updateAppointmentStatus(selectedAppointment, "Approved");
                JOptionPane.showMessageDialog(this, "Appointment approved!");
                loadPendingAppointments(lecturerUsername, appointmentsDropdown); // Refresh dropdown
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to approve.");
            }
        });

        // Reject appointment
        btnReject.addActionListener(e -> {
            String selectedAppointment = (String) appointmentsDropdown.getSelectedItem();
            if (selectedAppointment != null) {
                updateAppointmentStatus(selectedAppointment, "Rejected");
                JOptionPane.showMessageDialog(this, "Appointment rejected!");
                loadPendingAppointments(lecturerUsername, appointmentsDropdown); // Refresh dropdown
            } else {
                JOptionPane.showMessageDialog(this, "Please select an appointment to reject.");
            }
        });

        // View all appointments
        btnViewAll.addActionListener(e -> new AppointmentOverviewFrame(lecturerUsername));

        // Close frame
        btnClose.addActionListener(e -> dispose());

        // Layout for the buttons and dropdown
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.add(appointmentsDropdown);
        panel.add(btnApprove);
        panel.add(btnReject);
        panel.add(btnViewAll);
        panel.add(btnClose);

        panelBackground.add(panel, BorderLayout.CENTER);
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

    private void loadPendingAppointments(String lecturerUsername, JComboBox<String> appointmentsDropdown) {
        appointmentsDropdown.removeAllItems();
        File file = new File("resources/appointments.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No appointments found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> pendingAppointments = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equals(lecturerUsername) && parts[4].equalsIgnoreCase("Pending")) {
                    pendingAppointments.add(parts);
                }
            }

            // Sort pending appointments by date and time
            pendingAppointments.sort((a, b) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date dateA = sdf.parse(a[3]);
                    Date dateB = sdf.parse(b[3]);
                    return dateA.compareTo(dateB);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            });

            for (String[] appointment : pendingAppointments) {
                String display = String.format("Student: %s, Date: %s, Time: %s", appointment[0], appointment[3].substring(0, 10), appointment[3].substring(11));
                appointmentsDropdown.addItem(display);
            }

            if (pendingAppointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending appointments found.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments.");
        }
    }

    private void updateAppointmentStatus(String selectedAppointment, String status) {
        File inputFile = new File("resources/appointments.txt");
        File tempFile = new File("resources/appointments_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    // Format for display: "Student: {student}, Date: {date}, Time: {time}"
                    String displayFormat = String.format("Student: %s, Date: %s, Time: %s",
                            parts[0], parts[3].substring(0, 10), parts[3].substring(11));

                    if (displayFormat.equals(selectedAppointment)) {
                        // Update the status of the matched record
                        writer.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + status);
                    } else {
                        // Write the record as-is for non-matching appointments
                        writer.write(line);
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating appointment status.");
            e.printStackTrace();
        }

        // Replace the original file with the updated temporary file
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            JOptionPane.showMessageDialog(this, "Error finalizing appointment update.");
        }
    }
}