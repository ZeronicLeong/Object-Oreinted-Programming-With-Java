/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author jacky
 */
public class ViewRescheduleStatusFrame extends JFrame {
    public ViewRescheduleStatusFrame(String studentUsername) {
        setTitle("Reschedule Status - " + studentUsername);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Custom JPanel with a background image
        JPanel panelBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("resources/background.jpeg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelBackground.setLayout(new BorderLayout());

        // Create a JTextArea with transparent background
        JTextArea txtStatus = new JTextArea(15, 40) {
            @Override
            protected void paintComponent(Graphics g) {
                // Set transparency
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        txtStatus.setEditable(false);
        txtStatus.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font style
        txtStatus.setForeground(Color.WHITE); // Set text color
        txtStatus.setLineWrap(true); // Enable line wrapping
        txtStatus.setWrapStyleWord(true);

        // Wrap text area in a transparent scroll pane
        JScrollPane scrollPane = new JScrollPane(txtStatus);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Close button
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

        // Load reschedule status
        loadRescheduleStatus(studentUsername, txtStatus);

        // Add components to the background panel
        panelBackground.add(scrollPane, BorderLayout.CENTER);
        panelBackground.add(btnClose, BorderLayout.SOUTH);

        // Add the background panel to the frame
        add(panelBackground);

        // Make the frame visible
        setVisible(true);
    }

    private void loadRescheduleStatus(String studentUsername, JTextArea txtStatus) {
        txtStatus.setText("Reschedule Status:\n\n");
        File file = new File("resources/reschedule_requests.txt");

        if (!file.exists()) {
            txtStatus.append("No reschedule requests found. File does not exist.\n");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals(studentUsername)) {
                    found = true;
                    String lecturer = parts[1];
                    String oldSlot = parts[2];
                    String newSlot = parts[3];
                    String status = parts[4];

                    txtStatus.append(String.format("Lecturer: %s, Old Slot: %s, New Slot: %s, Status: %s\n",
                            lecturer, oldSlot, newSlot, status));
                }
            }

            if (!found) {
                txtStatus.append("No reschedule requests found for this user.\n");
            }
        } catch (IOException e) {
            txtStatus.append("Error loading reschedule status.\n");
            e.printStackTrace();
        }
    }
}
