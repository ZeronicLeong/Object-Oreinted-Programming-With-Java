/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author jacky
 */
public class AppointmentViewFrame extends JFrame {

    private final String studentUsername;

    public AppointmentViewFrame(String studentUsername) {
        this.studentUsername = studentUsername;

        setTitle("View Appointments");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
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
        JLabel lblTitle = new JLabel("Appointments Overview", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Table for displaying appointments
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tabs for Upcoming, Pending, and Rejected Appointments
        JScrollPane upcomingScrollPane = createAppointmentTable("Upcoming Appointments", "upcoming");
        JScrollPane pendingScrollPane = createAppointmentTable("Pending Appointments", "pending");
        JScrollPane rejectedScrollPane = createAppointmentTable("Rejected Appointments", "rejected");

        tabbedPane.addTab("Upcoming", upcomingScrollPane);
        tabbedPane.addTab("Pending", pendingScrollPane);
        tabbedPane.addTab("Rejected", rejectedScrollPane);

        panelBackground.add(tabbedPane, BorderLayout.CENTER);

        // Close Button
        JButton btnClose = new JButton("Close");
        btnClose.setBackground(Color.BLACK);
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        panelBackground.add(btnClose, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);
        setVisible(true);
    }

    private JScrollPane createAppointmentTable(String title, String filterType) {
        String[] columnNames = {"Lecturer", "Date", "Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        File file = new File("resources/appointments.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No appointments found.");
            return new JScrollPane(new JTable(model));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<String[]> appointments = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // Ensure all fields are parsed
                if (parts.length >= 5 && parts[0].equals(studentUsername)) {
                    String lecturer = parts[1];
                    String slot = parts[3];
                    String status = parts[4];

                    try {
                        // Include all approved appointments in the "upcoming" tab
                        if (filterType.equals("upcoming") && status.equalsIgnoreCase("Approved")) {
                            appointments.add(new String[]{lecturer, slot.substring(0, 10), slot.substring(11)});
                        } else if (filterType.equals("pending") && status.equalsIgnoreCase("Pending")) {
                            appointments.add(new String[]{lecturer, slot.substring(0, 10), slot.substring(11)});
                        } else if (filterType.equals("rejected") && status.equalsIgnoreCase("Rejected")) {
                            appointments.add(new String[]{lecturer, slot.substring(0, 10), slot.substring(11)});
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing date for appointment: " + line);
                    }
                }
            }

            // Sort the appointments by Lecturer, then Date, and Time
            appointments.sort((a, b) -> {
                int lecturerComparison = a[0].compareTo(b[0]); // Compare Lecturer
                if (lecturerComparison == 0) {
                    try {
                        Date dateA = sdf.parse(a[1] + " " + a[2]);
                        Date dateB = sdf.parse(b[1] + " " + b[2]);
                        return dateA.compareTo(dateB); // Compare Date and Time
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return lecturerComparison; // Sort by Lecturer if dates are equal
            });

            // Add sorted appointments to the table
            for (String[] appointment : appointments) {
                model.addRow(appointment);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments.");
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.getTableHeader().setReorderingAllowed(false);

        return new JScrollPane(table);
    }
}