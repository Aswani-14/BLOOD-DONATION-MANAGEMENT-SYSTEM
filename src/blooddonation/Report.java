package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Report extends JFrame implements ActionListener {

    private final JComboBox<Integer> campIdBox;
    private final JButton viewBtn;
    private final JTable table;
    private final DefaultTableModel model;

    public Report() {
        setTitle("Camp Report");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // for placing components over background

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/requestbg.jpg")); // put image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 800, 600);
        setContentPane(background);
        background.setLayout(null);

        // Top panel (camp selection & button)
        JLabel lblCampId = new JLabel("Select Camp ID:");
        lblCampId.setBounds(100, 20, 120, 25);
        lblCampId.setForeground(Color.WHITE); // if background is dark
        background.add(lblCampId);

        campIdBox = new JComboBox<>();
        campIdBox.setBounds(200, 20, 100, 25);
        loadCampIds();
        background.add(campIdBox);

        viewBtn = new JButton("View Report");
        viewBtn.setBounds(350, 20, 120, 25);
        viewBtn.addActionListener(this);
        background.add(viewBtn);

        // Table
        model = new DefaultTableModel(
                new String[]{"Camp ID", "Camp Name", "Location", "Date",
                        "Total Donors", "Total Volunteers", "Total Requests", "Blood Units"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 70, 600, 300);
        background.add(scrollPane);

        setVisible(true);
    }

    private void loadCampIds() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT camp_id FROM camp_table")) {
            while (rs.next()) {
                campIdBox.addItem(rs.getInt("camp_id"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading Camp IDs: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewBtn) {
            generateReport();
        }
    }

    private void generateReport() {
        int campId = (int) campIdBox.getSelectedItem();
        model.setRowCount(0);

        String sql = "SELECT c.camp_id, c.camp_name, c.location, c.date, " +
                "COALESCE(d.total_donors, 0) AS total_donors, " +
                "COALESCE(v.total_volunteers, 0) AS total_volunteers, " +
                "COALESCE(r.total_requests, 0) AS total_requests, " +
                "COALESCE(b.total_units, 0) AS blood_units " +
                "FROM camp_table c " +
                "LEFT JOIN (SELECT camp_id, COUNT(*) AS total_donors FROM donor_table GROUP BY camp_id) d ON c.camp_id = d.camp_id " +
                "LEFT JOIN (SELECT camp_id, COUNT(*) AS total_volunteers FROM volunteer_table GROUP BY camp_id) v ON c.camp_id = v.camp_id " +
                "LEFT JOIN (SELECT camp_id, COUNT(*) AS total_requests FROM request_form_table GROUP BY camp_id) r ON c.camp_id = r.camp_id " +
                "LEFT JOIN (SELECT camp_id, SUM(quantity) AS total_units FROM blood_stock_table GROUP BY camp_id) b ON c.camp_id = b.camp_id " +
                "WHERE c.camp_id=?";

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, campId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("camp_id"),
                        rs.getString("camp_name"),
                        rs.getString("location"),
                        rs.getString("date"),
                        rs.getInt("total_donors"),
                        rs.getInt("total_volunteers"),
                        rs.getInt("total_requests"),
                        rs.getInt("blood_units")
                });
            } else {
                JOptionPane.showMessageDialog(this, "No data available for this camp!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Report::new);
    }
}






