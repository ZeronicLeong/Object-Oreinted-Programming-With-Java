/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author jacky
 */
public class AppointmentBookingFrame extends JFrame {

    public AppointmentBookingFrame(String username) {
        setTitle("Book Appointment - " + username);
        setSize(500, 400);
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
        JLabel lblTitle = new JLabel("Book Appointment", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);  // Set text color to white
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel with fields
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4, 2, 10, 10));
        panelCenter.setOpaque(false); // Transparent background for the center panel

        JLabel lblLecturer = new JLabel("Select Lecturer:");
        lblLecturer.setForeground(Color.WHITE);  // Set text color to white
        JComboBox<String> lecturerDropdown = new JComboBox<>();
        JButton btnLoadSlots = new JButton("Load Slots");

        JLabel lblAvailableSlots = new JLabel("Available Slots:");
        lblAvailableSlots.setForeground(Color.WHITE);  // Set text color to white
        JComboBox<String> slotDropdown = new JComboBox<>();
        JButton btnBook = new JButton("Book Slot");
        JButton btnClose = new JButton("Close");

        // Load lecturers into the dropdown
        loadLecturers(lecturerDropdown);

        // Load available slots for selected lecturer
        btnLoadSlots.addActionListener(e -> {
            String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
            if (selectedLecturer != null) {
                loadAvailableSlots(selectedLecturer, slotDropdown);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lecturer.");
            }
        });

        // Book selected slot
        btnBook.addActionListener(e -> {
            String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
            String selectedSlot = (String) slotDropdown.getSelectedItem();
            if (selectedLecturer != null && selectedSlot != null) {
                try {
                    bookAppointment(username, selectedLecturer, selectedSlot);
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error booking appointment.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a lecturer and a slot.");
            }
        });

        // Close frame
        btnClose.addActionListener(e -> dispose());

        panelCenter.add(lblLecturer);
        panelCenter.add(lecturerDropdown);
        panelCenter.add(btnLoadSlots);
        panelCenter.add(new JLabel()); // Empty space
        panelCenter.add(lblAvailableSlots);
        panelCenter.add(slotDropdown);
        panelCenter.add(btnBook);
        panelCenter.add(btnClose);

        panelBackground.add(panelCenter, BorderLayout.CENTER);
        add(panelBackground, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadLecturers(JComboBox<String> lecturerDropdown) {
        lecturerDropdown.removeAllItems();
        File file = new File("resources/users.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No lecturers found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[2].equalsIgnoreCase("Lecturer")) {
                    lecturerDropdown.addItem(parts[0]); // Add only lecturer usernames
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading lecturers.");
        }
    }

    private void loadAvailableSlots(String lecturer, JComboBox<String> slotDropdown) {
        slotDropdown.removeAllItems(); // Clear previous entries
        File file = new File("resources/lecturers_slots.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file lecturer_slots.txt does not exist.");
            return;
        }

        Map<Date, String> sortedSlots = new TreeMap<>(); // TreeMap for automatic sorting by date

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip blank lines
                }

                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(lecturer.trim())) {
                    try {
                        Date slotDate = sdf.parse(parts[1].trim());
                        sortedSlots.put(slotDate, parts[1].trim()); // Add to TreeMap
                    } catch (Exception e) {
                        System.err.println("Invalid date format: " + parts[1].trim());
                    }
                }
            }

            if (sortedSlots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available slots found for " + lecturer + ".");
                return;
            }

            // Add sorted slots to the dropdown
            for (String slot : sortedSlots.values()) {
                slotDropdown.addItem(slot);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading slots: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookAppointment(String studentUsername, String lecturerUsername, String slot) throws IOException {
        File file = new File("resources/appointments.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Add an empty placeholder for the missing date field
            writer.write(studentUsername + "," + lecturerUsername + ",," + slot + ",Pending");
            writer.newLine(); // Ensure it writes to a new line
        }
    }

    // Utility function for rounded button style (if needed)
    public static void setRoundedButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(50, 50, 50));  // Black background
        button.setForeground(Color.WHITE);  // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}