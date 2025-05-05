/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author jacky
 */
public class StudentFeedbackFrame extends JFrame {
    private final String username;

    public StudentFeedbackFrame(String username) {
        this.username = username;

        setTitle("Provide Feedback");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background Panel
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
        JLabel lblTitle = new JLabel("Student Feedback", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel
        JPanel panelCenter = new JPanel(new GridLayout(5, 1, 10, 10));
        panelCenter.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel lblSelectLecturer = new JLabel("Select Lecturer:");
        lblSelectLecturer.setForeground(Color.WHITE);
        JComboBox<String> lecturerDropdown = new JComboBox<>();
        loadLecturers(lecturerDropdown);

        JLabel lblSelectAppointment = new JLabel("Select Appointment:");
        lblSelectAppointment.setForeground(Color.WHITE);
        JComboBox<String> appointmentDropdown = new JComboBox<>();
        lecturerDropdown.addActionListener(e -> loadAppointments((String) lecturerDropdown.getSelectedItem(), appointmentDropdown));

        inputPanel.add(lblSelectLecturer);
        inputPanel.add(lecturerDropdown);
        inputPanel.add(lblSelectAppointment);
        inputPanel.add(appointmentDropdown);

        panelCenter.add(inputPanel);

        // Feedback TextArea
        JTextArea txtFeedback = new JTextArea(5, 40);
        txtFeedback.setLineWrap(true);
        txtFeedback.setWrapStyleWord(true);
        txtFeedback.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        txtFeedback.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFeedback.setForeground(Color.DARK_GRAY);
        txtFeedback.setToolTipText("Write your feedback here...");
        JScrollPane feedbackScrollPane = new JScrollPane(txtFeedback);
        panelCenter.add(feedbackScrollPane);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton btnSubmitFeedback = new JButton("Submit Feedback");
        JButton btnViewLecturerFeedback = new JButton("View Lecturer Feedback");
        JButton btnClose = new JButton("Close");

        styleButton(btnSubmitFeedback);
        styleButton(btnViewLecturerFeedback);
        styleButton(btnClose);

        btnSubmitFeedback.addActionListener(e -> {
            String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();
            String feedback = txtFeedback.getText().trim();

            if (selectedLecturer == null || selectedAppointment == null || feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be completed.");
                return;
            }

            submitFeedback(selectedLecturer, selectedAppointment, feedback);
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            txtFeedback.setText("");
        });

        btnViewLecturerFeedback.addActionListener(e -> {
            String selectedLecturer = (String) lecturerDropdown.getSelectedItem();
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();

            if (selectedLecturer == null || selectedAppointment == null) {
                JOptionPane.showMessageDialog(this, "Please select a lecturer and appointment.");
                return;
            }

            String feedback = viewLecturerFeedback(username, selectedLecturer, selectedAppointment);
            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No feedback found from the lecturer for this appointment.");
            } else {
                JOptionPane.showMessageDialog(this, "Lecturer Feedback: " + feedback);
            }
        });

        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnSubmitFeedback);
        buttonPanel.add(btnViewLecturerFeedback);
        buttonPanel.add(btnClose);

        panelCenter.add(buttonPanel);

        // Add Panels
        panelBackground.add(panelCenter, BorderLayout.CENTER);
        add(panelBackground, BorderLayout.CENTER);
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 120, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    private void loadLecturers(JComboBox<String> lecturerDropdown) {
        lecturerDropdown.removeAllItems();
        File file = new File("resources/lecturers_slots.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file lecturers_slots.txt does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Set<String> lecturers = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length > 0) {
                    String lecturer = parts[0].trim();
                    if (lecturers.add(lecturer)) {
                        lecturerDropdown.addItem(lecturer);
                    }
                }
            }

            if (lecturers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No lecturers found in lecturers_slots.txt.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading lecturers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAppointments(String selectedLecturer, JComboBox<String> appointmentDropdown) {
        appointmentDropdown.removeAllItems();
        if (selectedLecturer == null) return;

        File file = new File("resources/appointments.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No appointments found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> appointments = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(username) && parts[1].equals(selectedLecturer) && parts[4].equalsIgnoreCase("Approved")) {
                    appointments.add(parts);
                }
            }

            appointments.sort((a, b) -> {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date dateA = sdf.parse(a[3]);
                    Date dateB = sdf.parse(b[3]);
                    return dateA.compareTo(dateB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            });

            for (String[] appointment : appointments) {
                String display = String.format("Date: %s, Time: %s",
                        appointment[3].substring(0, 10), appointment[3].substring(11));
                appointmentDropdown.addItem(display);
            }

            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No approved appointments found.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments.");
            e.printStackTrace();
        }
    }

    private void submitFeedback(String lecturer, String appointmentDisplay, String feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/feedback.txt", true))) {
            String[] appointmentParts = appointmentDisplay.split(", ");
            String date = appointmentParts[0].replace("Date: ", "").trim();
            String time = appointmentParts[1].replace("Time: ", "").trim();
            String appointment = date + " " + time;

            writer.write(username + "," + lecturer + "," + appointment + "," + feedback);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving feedback.");
            e.printStackTrace();
        }
    }

    private String viewLecturerFeedback(String studentUsername, String lecturer, String appointmentDisplay) {
        File file = new File("resources/feedback.txt");
        if (!file.exists()) return "";

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String[] appointmentParts = appointmentDisplay.split(", ");
            String date = appointmentParts[0].replace("Date: ", "").trim();
            String time = appointmentParts[1].replace("Time: ", "").trim();
            String appointment = date + " " + time;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(lecturer) && parts[1].equals(studentUsername) && parts[2].equals(appointment)) {
                    return parts[3];
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading feedback.");
            e.printStackTrace();
        }
        return "";
    }
}