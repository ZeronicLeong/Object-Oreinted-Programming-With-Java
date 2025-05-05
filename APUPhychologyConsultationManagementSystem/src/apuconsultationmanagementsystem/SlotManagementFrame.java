/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author jacky
 */
public class SlotManagementFrame extends JFrame {
    private String[] slots = new String[50];
    private int slotCount = 0;
    private JDateChooser dateChooser;
    private JComboBox<String> timeDropdown;
    private JComboBox<String> slotDropdown;

    public SlotManagementFrame(String username) {
        setTitle("Manage Slots - " + username);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up main panel with a modern layout
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("resources/background.jpeg");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Title Section
        JLabel lblTitle = new JLabel("Slot Management", JLabel.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Input Section
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel lblDate = new JLabel("Select Date:");
        lblDate.setForeground(Color.WHITE);
        dateChooser = new JDateChooser();

        JLabel lblTime = new JLabel("Select Time:");
        lblTime.setForeground(Color.WHITE);
        timeDropdown = new JComboBox<>(generateTimeSlots());

        JLabel lblSelectSlot = new JLabel("Select Slot to Remove:");
        lblSelectSlot.setForeground(Color.WHITE);
        slotDropdown = new JComboBox<>();

        inputPanel.add(lblDate);
        inputPanel.add(dateChooser);
        inputPanel.add(lblTime);
        inputPanel.add(timeDropdown);
        inputPanel.add(lblSelectSlot);
        inputPanel.add(slotDropdown);

        mainPanel.add(inputPanel, BorderLayout.WEST);

        // Slot Display Area
        JTextArea txtSlots = new JTextArea(10, 30);
        txtSlots.setEditable(false);
        txtSlots.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSlots.setForeground(Color.BLACK);
        txtSlots.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent background
        JScrollPane scrollPane = new JScrollPane(txtSlots);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Available Slots"));

        // Add slot display to the center of the layout
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout());

        JButton btnAddSlot = new JButton("Add Slot");
        JButton btnRemoveSlot = new JButton("Remove Slot");
        JButton btnClose = new JButton("Close");

        styleButton(btnAddSlot);
        styleButton(btnRemoveSlot);
        styleButton(btnClose);

        buttonPanel.add(btnAddSlot);
        buttonPanel.add(btnRemoveSlot);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load slots
        loadSlots(username, txtSlots);

        // Add slot action
        btnAddSlot.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            String selectedTime = (String) timeDropdown.getSelectedItem();

            if (selectedDate == null || selectedTime == null) {
                JOptionPane.showMessageDialog(this, "Please select a date and time.");
                return;
            }

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            String slot = formattedDate + " " + selectedTime;

            if (ValidationUtils.isValidTime(slot)) {
                if (slotCount < slots.length) {
                    slots[slotCount++] = slot;
                    saveSlot(username, slot);
                    loadSlots(username, txtSlots);
                    JOptionPane.showMessageDialog(this, "Slot added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Slot limit reached.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid slot time. Time must be between 7 AM and 6 PM.");
            }
        });

        // Remove slot action
        btnRemoveSlot.addActionListener(e -> {
            String selectedSlot = (String) slotDropdown.getSelectedItem();

            if (selectedSlot == null || selectedSlot.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a slot to remove.");
                return;
            }

            removeSlot(username, selectedSlot);
            loadSlots(username, txtSlots);
            JOptionPane.showMessageDialog(this, "Slot removed successfully!");
        });

        // Close button action
        btnClose.addActionListener(e -> dispose());

        // Add main panel to the frame
        add(mainPanel);

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    private String[] generateTimeSlots() {
        String[] timeSlots = new String[22];
        for (int i = 0; i < 22; i++) {
            int hours = 7 + i / 2;
            int minutes = (i % 2) * 30;
            timeSlots[i] = String.format("%02d:%02d", hours, minutes);
        }
        return timeSlots;
    }

    private void loadSlots(String username, JTextArea txtSlots) {
        txtSlots.setText("Your Slots:\n");
        slotDropdown.removeAllItems();
        List<String> slotsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/lecturers_slots.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    slotsList.add(parts[1]); // Add slot data for the user
                }
            }

            // Sort slots by date and time
            slotsList.sort((slot1, slot2) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date1 = sdf.parse(slot1);
                    Date date2 = sdf.parse(slot2);
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    // Handle parsing error
                    System.err.println("Error parsing slot date: " + e.getMessage());
                    return 0;
                }
            });

            // Display sorted slots
            for (String slot : slotsList) {
                txtSlots.append(slot + "\n");
                slotDropdown.addItem(slot);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading slots.");
            e.printStackTrace();
        }
    }

    private void saveSlot(String username, String slot) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/lecturers_slots.txt", true))) {
            writer.write(username + "," + slot);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeSlot(String username, String slot) {
        File inputFile = new File("resources/lecturers_slots.txt");
        File tempFile = new File("resources/temp_lecturers_slots.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!(parts[0].equals(username) && parts[1].equals(slot))) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error removing slot.");
            e.printStackTrace();
        }

        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            System.out.println("Slot removed successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating slots file.");
        }
    }
}