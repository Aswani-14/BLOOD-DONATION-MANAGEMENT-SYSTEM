package blooddonation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {

    JLabel userLabel, passLabel, roleLabel;
    JTextField userText;
    JPasswordField passText;
    JComboBox<String> roleBox;
    JButton loginBtn, clearBtn;

    Connection con;

    public LoginFrame() {
        setTitle("Blood Donation Management System - Login");
        setSize(600, 400);  // reduced window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // we'll set absolute positions

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/background.jpg"));
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 600, 400);
        add(background);

        // Labels and fields (smaller width)
        roleLabel = new JLabel("Role:");
        roleLabel.setBounds(150, 50, 80, 25);
        background.add(roleLabel);

        String[] roles = {"Admin", "Organization"};
        roleBox = new JComboBox<>(roles);
        roleBox.setBounds(250, 50, 150, 25);
        background.add(roleBox);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(150, 100, 80, 25);
        background.add(userLabel);

        userText = new JTextField();
        userText.setBounds(250, 100, 150, 25);
        background.add(userText);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(150, 150, 80, 25);
        background.add(passLabel);

        passText = new JPasswordField();
        passText.setBounds(250, 150, 150, 25);
        background.add(passText);

        // Buttons
        loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 220, 90, 30);
        loginBtn.addActionListener(this);
        background.add(loginBtn);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(300, 220, 90, 30);
        clearBtn.addActionListener(this);
        background.add(clearBtn);

        // DB Connection
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/login_schema",
                    "root", "Aswani@14N");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = userText.getText();
            String password = String.valueOf(passText.getPassword());
            String role = (String) roleBox.getSelectedItem();

            try {
                String sql = "SELECT l.login_id, o.org_id, l.role " +
                        "FROM login_table l " +
                        "LEFT JOIN organization_table o ON l.login_id = o.login_id " +
                        "WHERE l.username=? AND l.password=? AND l.role=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");

                    if (role.equals("Admin")) {
                        new AdminHome();
                        dispose();
                    } else if (role.equals("Organization")) {
                        int orgId = rs.getInt("org_id");
                        new RequestForm(orgId);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == clearBtn) {
            userText.setText("");
            passText.setText("");
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
