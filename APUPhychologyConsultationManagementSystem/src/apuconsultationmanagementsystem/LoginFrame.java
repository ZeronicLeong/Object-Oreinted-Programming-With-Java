/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.border.Border;
/**
 *
 * @author jacky
 */
public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login - APU Psychology Consultation System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Login System", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

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

        // Center Panel with username and password fields
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(2, 2, 10, 10));
        panelCenter.setOpaque(false);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE);
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE);
        JPasswordField txtPassword = new JPasswordField();

        panelCenter.add(lblUsername);
        panelCenter.add(txtUsername);
        panelCenter.add(lblPassword);
        panelCenter.add(txtPassword);
        panelBackground.add(panelCenter, BorderLayout.CENTER);

        // Bottom Panel with Login and Register buttons
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(1, 2, 10, 10));
        panelBottom.setOpaque(false);

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        // Set button appearance
        setRoundedButton(btnLogin);
        setRoundedButton(btnRegister);

        panelBottom.add(btnLogin);
        panelBottom.add(btnRegister);
        panelBackground.add(panelBottom, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);

        // Login Button Action Listener
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            // Authenticate the user and retrieve the role
            String role = authenticate(username, password);
            if (role != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();

                // Open the appropriate dashboard based on the role
                if (role.equalsIgnoreCase("Student")) {
                    new StudentDashboardFrame(username);
                } else if (role.equalsIgnoreCase("Lecturer")) {
                    new LecturerDashboardFrame(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid role in database.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        // Register Button Action Listener
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterFrame(); // Open Register Frame
        });

        setVisible(true);
    }

    // Authenticate user and return role if successful
    private String authenticate(String username, String password) {
        File file = new File("resources/users.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Users database not found.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();
                    String fileRole = parts[2].trim();

                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return fileRole; // Return the role if credentials match
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading user database.");
            e.printStackTrace();
        }
        return null; // Return null if no match found
    }

    // Utility function for rounded button style
    public static void setRoundedButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}