/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apuconsultationmanagementsystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
/**
 *
 * @author jacky
 */
public class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("Register");
        setSize(400, 350);
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
        JLabel lblTitle = new JLabel("Register System", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE); // Set text color to white
        panelBackground.add(lblTitle, BorderLayout.NORTH);

        // Center Panel for username and password fields
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4, 2, 10, 10));
        panelCenter.setOpaque(false); // Set the center panel to transparent

        JLabel lblRole = new JLabel("Role:");
        lblRole.setForeground(Color.WHITE); // Set text color to white
        String[] roles = {"Student", "Lecturer"};
        JComboBox<String> cbRole = new JComboBox<>(roles);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setForeground(Color.WHITE); // Set text color to white
        JTextField txtUsername = new JTextField();

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setForeground(Color.WHITE); // Set text color to white
        JPasswordField txtPassword = new JPasswordField();

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setForeground(Color.WHITE); // Set text color to white
        JPasswordField txtConfirmPassword = new JPasswordField();

        panelCenter.add(lblRole);
        panelCenter.add(cbRole);
        panelCenter.add(lblUsername);
        panelCenter.add(txtUsername);
        panelCenter.add(lblPassword);
        panelCenter.add(txtPassword);
        panelCenter.add(lblConfirmPassword);
        panelCenter.add(txtConfirmPassword);
        panelBackground.add(panelCenter, BorderLayout.CENTER);

        // Bottom Panel for Register and Cancel buttons
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(1, 2, 10, 10));
        panelBottom.setOpaque(false); // Set the bottom panel to transparent

        JButton btnRegister = new JButton("Register");
        Utils.setRoundedButton(btnRegister); // Apply the rounded button style
        JButton btnCancel = new JButton("Cancel");
        Utils.setRoundedButton(btnCancel); // Apply the rounded button style

        panelBottom.add(btnRegister);
        panelBottom.add(btnCancel);
        panelBackground.add(panelBottom, BorderLayout.SOUTH);

        add(panelBackground, BorderLayout.CENTER);

        // Register Button Action Listener
        btnRegister.addActionListener(e -> {
            String role = (String) cbRole.getSelectedItem();
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
            } else {
                if (registerUser(role, username, password)) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    dispose();
                    new LoginFrame();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. User might already exist.");
                }
            }
        });

        // Cancel Button Action Listener
        btnCancel.addActionListener(e -> {
            dispose();
            new LoginFrame(); // Return to Login Frame
        });

        setVisible(true);
    }

    // Method to save the registered user
    private boolean registerUser(String role, String username, String password) {
        File file = new File("resources/users.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Ensure the file ends with a newline before appending a new user
            if (file.length() > 0) {
                writer.newLine(); // Add a newline if the file is not empty
            }
            writer.write(username + "," + password + "," + role); // Write the user data
            return true; // Successfully saved user
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user information.");
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
}
