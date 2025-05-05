/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author jacky
 */
public class RescheduleApprovalFrame extends JFrame {

    private JComboBox<String> requestsDropdown;
    private Map<String, String> requestMap = new HashMap<>();

    public RescheduleApprovalFrame(String lecturerName) {
        setTitle("Approve Reschedule Requests");
        setSize(600, 400);
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
        JLabel lblTitle = new JLabel("Pending Reschedule Requests", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE); // Set text color to white
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Dropdown menu for requests
        JLabel lblDropdown = new JLabel("Select Request:");
        lblDropdown.setForeground(Color.WHITE); // Set text color to white
        requestsDropdown = new JComboBox<>();
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setOpaque(false);
        dropdownPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dropdownPanel.add(lblDropdown);
        dropdownPanel.add(requestsDropdown);
        panelBackground.add(dropdownPanel, BorderLayout.CENTER);

        // Buttons for actions
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");
        JButton btnClose = new JButton("Close");

        setButtonStyle(btnApprove);
        setButtonStyle(btnReject);
        setButtonStyle(btnClose);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnClose);

        panelBackground.add(buttonPanel, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);

        loadRequests(lecturerName);

        btnApprove.addActionListener(e -> processRequest(lecturerName, true));
        btnReject.addActionListener(e -> processRequest(lecturerName, false));
        btnClose.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void setButtonStyle(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(51, 51, 51)); // Light black background
        button.setForeground(Color.WHITE);          // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadRequests(String lecturerName) {
        requestsDropdown.removeAllItems();
        requestMap.clear();

        File file = new File("resources/reschedule_requests.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No reschedule requests found.");
            return;
        }

        List<String[]> requests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[1].equals(lecturerName) && parts[4].equalsIgnoreCase("Pending")) {
                    requests.add(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading requests.");
        }

        // Sort by new slot date and time
        requests.sort((a, b) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dateA = sdf.parse(a[3]);
                Date dateB = sdf.parse(b[3]);
                return dateA.compareTo(dateB);
            } catch (Exception e) {
                return 0;
            }
        });

        for (String[] request : requests) {
            String display = String.format("Student: %s, Old Slot: %s, New Slot: %s",
                    request[0], request[2], request[3]);
            requestMap.put(display, String.join(",", request));
            requestsDropdown.addItem(display);
        }

        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending requests to display.");
        }
    }

    private void processRequest(String lecturerName, boolean approve) {
        String selectedRequest = (String) requestsDropdown.getSelectedItem();
        if (selectedRequest == null) {
            JOptionPane.showMessageDialog(this, "No request selected.");
            return;
        }

        String originalRequest = requestMap.get(selectedRequest);
        if (originalRequest == null) {
            JOptionPane.showMessageDialog(this, "Request not found.");
            return;
        }

        File inputFile = new File("resources/reschedule_requests.txt");
        File tempFile = new File("resources/temp_reschedule_requests.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(originalRequest)) {
                    String[] parts = originalRequest.split(",");
                    if (approve) {
                        saveNewAppointment(parts[0], parts[1], parts[3]);
                        writer.write(String.join(",", parts[0], parts[1], parts[2], parts[3], "Approved"));
                    } else {
                        writer.write(String.join(",", parts[0], parts[1], parts[2], parts[3], "Rejected"));
                    }
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing the request.");
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            JOptionPane.showMessageDialog(this, "Error updating the requests file.");
        }

        JOptionPane.showMessageDialog(this, approve ? "Request approved!" : "Request rejected!");
        loadRequests(lecturerName);
    }

    private void saveNewAppointment(String studentUsername, String lecturerName, String newSlot) {
        File file = new File("resources/appointments.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(studentUsername + "," + lecturerName + ",," + newSlot + ",Approved");
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving new appointment.");
        }
    }
}