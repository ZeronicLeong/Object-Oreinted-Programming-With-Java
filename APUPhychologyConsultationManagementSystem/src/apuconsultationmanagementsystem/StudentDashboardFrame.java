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
public class StudentDashboardFrame extends JFrame {

    public StudentDashboardFrame(String studentUsername) {
        setTitle("Student Dashboard - " + studentUsername);
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
        JLabel lblTitle = new JLabel("Student Dashboard", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel with buttons for functionalities
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(5, 1, 10, 10));
        panelCenter.setOpaque(false);

        JButton btnBookAppointment = new JButton("Book Appointment");
        JButton btnViewAppointments = new JButton("View Appointments");
        JButton btnCancelReschedule = new JButton("Cancel/Reschedule Appointment");
        JButton btnViewRescheduleStatus = new JButton("View Reschedule Status");
        JButton btnProvideFeedback = new JButton("Provide Feedback");
        JButton btnLogout = new JButton("Logout");

        setRoundedButton(btnBookAppointment);
        setRoundedButton(btnViewAppointments);
        setRoundedButton(btnCancelReschedule);
        setRoundedButton(btnViewRescheduleStatus);
        setRoundedButton(btnProvideFeedback);
        setRoundedButton(btnLogout);

        panelCenter.add(btnBookAppointment);
        panelCenter.add(btnViewAppointments);
        panelCenter.add(btnCancelReschedule);
        panelCenter.add(btnViewRescheduleStatus);
        panelCenter.add(btnProvideFeedback);
        panelCenter.add(btnLogout);

        panelBackground.add(panelCenter, BorderLayout.CENTER);

        // Add background panel to the frame
        add(panelBackground, BorderLayout.CENTER);

        // Button Actions
        btnBookAppointment.addActionListener(e -> new AppointmentBookingFrame(studentUsername));
        btnViewAppointments.addActionListener(e -> new AppointmentViewFrame(studentUsername));
        btnCancelReschedule.addActionListener(e -> new CancelRescheduleFrame(studentUsername));
        btnViewRescheduleStatus.addActionListener(e -> new ViewRescheduleStatusFrame(studentUsername));
        btnProvideFeedback.addActionListener(e -> new StudentFeedbackFrame(studentUsername)); // Redirect to StudentFeedbackFrame
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