/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author jacky
 */
public class LecturerDashboardFrame extends JFrame {

    public LecturerDashboardFrame(String username) {
        setTitle("Lecturer Dashboard - " + username);
        setSize(500, 400);
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
        JLabel lblTitle = new JLabel("Lecturer Dashboard", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE); // White text color
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel with buttons for functionalities
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(5, 1, 10, 10));
        panelCenter.setOpaque(false); // Transparent background for the center panel

        JButton btnManageSlots = new JButton("Manage Slots");
        JButton btnViewAppointments = new JButton("View Appointments");
        JButton btnRescheduleRequests = new JButton("Reschedule Requests");
        JButton btnViewFeedback = new JButton("View Feedback");
        JButton btnLogout = new JButton("Logout");

        setRoundedButton(btnManageSlots);
        setRoundedButton(btnViewAppointments);
        setRoundedButton(btnRescheduleRequests);
        setRoundedButton(btnViewFeedback);
        setRoundedButton(btnLogout);

        panelCenter.add(btnManageSlots);
        panelCenter.add(btnViewAppointments);
        panelCenter.add(btnRescheduleRequests);
        panelCenter.add(btnViewFeedback);
        panelCenter.add(btnLogout);

        panelBackground.add(panelCenter, BorderLayout.CENTER);

        // Add background panel to the frame
        add(panelBackground, BorderLayout.CENTER);

        // Button Actions
        btnManageSlots.addActionListener(e -> new SlotManagementFrame(username));

        btnViewAppointments.addActionListener(e -> new LecturerAppointmentViewFrame(username));

        btnRescheduleRequests.addActionListener(e -> new RescheduleApprovalFrame(username));

        btnViewFeedback.addActionListener(e -> new LecturerFeedbackFrame(username)); // Redirect to LecturerFeedbackFrame

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame(); // Redirect to login frame
        });

        setVisible(true);
    }

    // Utility function for rounded button style
    public static void setRoundedButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(50, 50, 50)); // Black background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
