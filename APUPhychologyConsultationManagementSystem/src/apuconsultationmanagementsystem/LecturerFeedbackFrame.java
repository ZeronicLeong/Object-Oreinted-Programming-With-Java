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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
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
public class LecturerFeedbackFrame extends JFrame {
    private final String username;

    public LecturerFeedbackFrame(String username) {
        this.username = username;

        setTitle("Lecturer Feedback Management");
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
        JLabel lblTitle = new JLabel("Lecturer Feedback Management", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel
        JPanel panelCenter = new JPanel(new GridLayout(6, 1, 10, 10));
        panelCenter.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setOpaque(false);

        JLabel lblSelectStudent = new JLabel("Select Student:");
        lblSelectStudent.setForeground(Color.WHITE);
        JComboBox<String> studentDropdown = new JComboBox<>();
        loadStudents(studentDropdown);

        JLabel lblSelectAppointment = new JLabel("Select Appointment:");
        lblSelectAppointment.setForeground(Color.WHITE);
        JComboBox<String> appointmentDropdown = new JComboBox<>();
        studentDropdown.addActionListener(e -> loadAppointments((String) studentDropdown.getSelectedItem(), appointmentDropdown));

        inputPanel.add(lblSelectStudent);
        inputPanel.add(studentDropdown);
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
        txtFeedback.setToolTipText("Write your feedback for the student here...");
        JScrollPane feedbackScrollPane = new JScrollPane(txtFeedback);
        panelCenter.add(feedbackScrollPane);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton btnSubmitFeedback = new JButton("Submit Feedback");
        JButton btnViewFeedback = new JButton("View Feedback");
        JButton btnClose = new JButton("Close");

        styleButton(btnSubmitFeedback);
        styleButton(btnViewFeedback);
        styleButton(btnClose);

        btnSubmitFeedback.addActionListener(e -> {
            String selectedStudent = (String) studentDropdown.getSelectedItem();
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();
            String feedback = txtFeedback.getText().trim();

            if (selectedStudent == null || selectedAppointment == null || feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be completed.");
                return;
            }

            submitFeedback(selectedStudent, selectedAppointment, feedback);
            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
            txtFeedback.setText("");
        });

        btnViewFeedback.addActionListener(e -> {
            String selectedStudent = (String) studentDropdown.getSelectedItem();
            String selectedAppointment = (String) appointmentDropdown.getSelectedItem();

            if (selectedStudent == null || selectedAppointment == null) {
                JOptionPane.showMessageDialog(this, "Please select a student and appointment.");
                return;
            }

            String feedback = viewFeedback(selectedStudent, selectedAppointment);
            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No feedback found for the selected appointment.");
            } else {
                JOptionPane.showMessageDialog(this, "Feedback: " + feedback);
            }
        });

        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnSubmitFeedback);
        buttonPanel.add(btnViewFeedback);
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

    private void loadStudents(JComboBox<String> studentDropdown) {
        studentDropdown.removeAllItems();
        File file = new File("resources/appointments.txt");

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Appointments file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Set<String> students = new TreeSet<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(username)) {
                    students.add(parts[0]);
                }
            }
            for (String student : students) {
                studentDropdown.addItem(student);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading students.");
            e.printStackTrace();
        }
    }

    private void loadAppointments(String selectedStudent, JComboBox<String> appointmentDropdown) {
        appointmentDropdown.removeAllItems();
        if (selectedStudent == null) return;

        File file = new File("resources/appointments.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Appointments file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String[]> appointments = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(selectedStudent) && parts[1].equals(username) && parts[4].equalsIgnoreCase("Approved")) {
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
                String display = String.format("%s", appointment[3]); // Date and Time only
                appointmentDropdown.addItem(display);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments.");
            e.printStackTrace();
        }
    }

    private void submitFeedback(String student, String appointment, String feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/feedback.txt", true))) {
            writer.write(username + "," + student + "," + appointment + "," + feedback);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving feedback.");
            e.printStackTrace();
        }
    }

    private String viewFeedback(String student, String appointment) {
        File file = new File("resources/feedback.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Feedback file not found.");
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4); // Ensure all fields are parsed
                if (parts.length >= 4) {
                    String feedbackProvider = parts[0].trim(); // Student providing feedback
                    String feedbackReceiver = parts[1].trim(); // Lecturer receiving feedback
                    String feedbackAppointment = parts[2].trim(); // Appointment Date and Time
                    String feedbackContent = parts[3].trim(); // Feedback Content

                    // Match the record: student -> feedback provider, lecturer -> feedback receiver, appointment -> time and date
                    if (feedbackProvider.equals(student) &&
                        feedbackReceiver.equals(username) &&
                        feedbackAppointment.equals(appointment)) {
                        return feedbackContent; // Return feedback if all fields match
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading feedback file.");
            e.printStackTrace();
        }

        return "No feedback found for the selected appointment."; // Return message if no matching feedback is found
    }
}
