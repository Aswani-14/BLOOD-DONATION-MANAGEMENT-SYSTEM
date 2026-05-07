package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Request extends JFrame {
    private JButton btnSelect, btnAccept, btnReject;
    private Integer selectedRequestId = null; // store selected request_id

    public Request() {
        setTitle("Admin Request Management");
        setSize(500, 300); // bigger to fit background nicely
        setLocationRelativeTo(null);
        setLayout(null);

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/requestbg.jpg")); // put image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 500, 300);
        setContentPane(background);
        background.setLayout(null);

        // Buttons
        btnSelect = new JButton("Select Request");
        btnSelect.setBounds(150, 50, 150, 30);
        background.add(btnSelect);

        btnAccept = new JButton("Accept");
        btnAccept.setBounds(100, 100, 100, 30);
        background.add(btnAccept);

        btnReject = new JButton("Reject");
        btnReject.setBounds(250, 100, 100, 30);
        background.add(btnReject);

        // Button actions
        btnSelect.addActionListener(e -> selectRequest());
        btnAccept.addActionListener(e -> updateStatus("Accepted"));
        btnReject.addActionListener(e -> updateStatus("Rejected"));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void selectRequest() {
        String sql = "SELECT r.request_id, o.OrgName, r.blood_group, r.quantity, r.status " +
                "FROM request_form_table r " +
                "JOIN organization_table o ON r.org_id = o.org_id";

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Request ID", "Organization", "Blood Group", "Quantity", "Status"},0
            );

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getString("OrgName"),
                        rs.getString("blood_group"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                });
            }

            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            int option = JOptionPane.showConfirmDialog(this, new JScrollPane(table),
                    "Select a Request", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(option == JOptionPane.OK_OPTION) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow != -1) {
                    selectedRequestId = (Integer) table.getValueAt(selectedRow, 0);
                    JOptionPane.showMessageDialog(this,
                            "Selected Request ID: " + selectedRequestId);
                } else {
                    JOptionPane.showMessageDialog(this, "No request selected!");
                }
            }

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    private void updateStatus(String status) {
        if(selectedRequestId == null) {
            JOptionPane.showMessageDialog(this, "Select a request first!");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
             PreparedStatement pst = con.prepareStatement(
                     "UPDATE request_form_table SET status=? WHERE request_id=?")) {

            pst.setString(1, status);
            pst.setInt(2, selectedRequestId);

            int rows = pst.executeUpdate();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "Request " + status + " successfully!");
                selectedRequestId = null; // reset after action
            } else {
                JOptionPane.showMessageDialog(this, "Error updating request!");
            }

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Request::new);
    }
}




