package blooddonation;

import javax.swing.*;
import java.awt.*;
import blooddonation.LoginFrame;

public class AdminHome extends JFrame {

    public AdminHome() {
        setTitle("Admin - Home");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // We'll use absolute positioning

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/adminbg.jpeg")); // replace with your image path
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 700, 400);
        setContentPane(background);

        // Components
        JLabel header = new JLabel(" Blood Donation - Admin Dashboard");
        header.setBounds(20, 10, 400, 30);
        header.setForeground(Color.WHITE); // if image is dark, make text visible
        background.add(header);

        JButton btnCamp = new JButton("Camp");
        btnCamp.setBounds(30, 70, 140, 40);
        background.add(btnCamp);

        JButton btnDonor = new JButton("Donor");
        btnDonor.setBounds(200, 70, 140, 40);
        background.add(btnDonor);

        JButton btnOrg = new JButton("Organization");
        btnOrg.setBounds(370, 70, 140, 40);
        background.add(btnOrg);

        JButton btnVolunteer = new JButton("Volunteer");
        btnVolunteer.setBounds(30, 140, 140, 40);
        background.add(btnVolunteer);

        JButton btnStock = new JButton("Blood Stock");
        btnStock.setBounds(200, 140, 140, 40);
        background.add(btnStock);

        JButton btnRequest = new JButton("Requests");
        btnRequest.setBounds(370, 140, 140, 40);
        background.add(btnRequest);

        JButton btnReport = new JButton("Reports");
        btnReport.setBounds(30, 210, 140, 40);
        background.add(btnReport);

        JButton btnExit = new JButton("Exit");
        btnExit.setBounds(200, 210, 140, 40);
        background.add(btnExit);

        // Button actions
        btnCamp.addActionListener(e -> new Camp());
        btnDonor.addActionListener(e -> new Donor());
        btnOrg.addActionListener(e -> new Organization());
        btnVolunteer.addActionListener(e -> new Volunteer());
        btnStock.addActionListener(e -> new BloodStock());
        btnRequest.addActionListener(e -> new Request());
        btnReport.addActionListener(e -> new Report());
        btnExit.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "Logout and return to login?", "Exit", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminHome();
    }
}

