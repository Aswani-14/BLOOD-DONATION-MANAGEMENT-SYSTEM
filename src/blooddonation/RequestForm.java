package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RequestForm extends JFrame {
    private int orgId;
    private JTextField txtQuantity, txtDate;
    private JComboBox<String> cbBloodGroup;
    private JButton btnSubmit, btnView, btnExit;
    private DefaultTableModel model;
    private JTable table;

    public RequestForm(int orgId) {
        this.orgId = orgId;

        setTitle("Organization Request Form");
        setSize(600, 500);
        setLayout(null);
        setLocationRelativeTo(null);

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/request.jpg")); // put image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 600, 500);
        setContentPane(background);
        background.setLayout(null);

        // Labels & Fields
        JLabel lblBloodGroup = new JLabel("Blood Group:");
        lblBloodGroup.setBounds(30, 20, 100, 25);
        lblBloodGroup.setForeground(Color.BLACK);
        background.add(lblBloodGroup);

        cbBloodGroup = new JComboBox<>(new String[]{"A+","A-","B+","B-","O+","O-","AB+","AB-"});
        cbBloodGroup.setBounds(140, 20, 100, 25);
        background.add(cbBloodGroup);

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(30, 60, 100, 25);
        lblQuantity.setForeground(Color.BLACK);
        background.add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(140, 60, 100, 25);
        background.add(txtQuantity);

        JLabel lblDate = new JLabel("Request Date (YYYY-MM-DD):");
        lblDate.setBounds(30, 100, 200, 25);
        lblDate.setForeground(Color.BLACK);
        background.add(lblDate);

        txtDate = new JTextField();
        txtDate.setBounds(230, 100, 100, 25);
        background.add(txtDate);

        // Buttons
        btnSubmit = new JButton("Submit Request");
        btnSubmit.setBounds(30, 140, 150, 30);
        background.add(btnSubmit);

        btnView = new JButton("View My Requests");
        btnView.setBounds(200, 140, 150, 30);
        background.add(btnView);

        btnExit = new JButton("Exit");
        btnExit.setBounds(370, 140, 100, 30);
        background.add(btnExit);

        // Table (initially hidden)
        model = new DefaultTableModel(new String[]{"Request ID", "Blood Group", "Quantity", "Date", "Status"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 190, 520, 250);
        sp.setVisible(false); // hide table initially
        background.add(sp);

        // Button actions
        btnSubmit.addActionListener(e -> submitRequest(sp));
        btnView.addActionListener(e -> loadRequests(sp));
        btnExit.addActionListener(e -> { dispose(); new LoginFrame(); });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void submitRequest(JScrollPane sp) {
        String bloodGroup = (String) cbBloodGroup.getSelectedItem();
        String quantityStr = txtQuantity.getText().trim();
        String date = txtDate.getText().trim();

        if(bloodGroup.isEmpty() || quantityStr.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this,"All fields are required!");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N")) {

            // Ensure orgId exists in organization_table
            PreparedStatement pstCheck = con.prepareStatement("SELECT * FROM organization_table WHERE org_id=?");
            pstCheck.setInt(1, orgId);
            ResultSet rsCheck = pstCheck.executeQuery();
            if(!rsCheck.next()) {
                JOptionPane.showMessageDialog(this,"Organization not found! Cannot submit request.");
                return;
            }

            // Insert request
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO request_form_table (org_id, blood_group, quantity, request_date, status) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pst.setInt(1, orgId);
            pst.setString(2, bloodGroup);
            pst.setInt(3, Integer.parseInt(quantityStr));
            pst.setString(4, date);
            pst.setString(5, "Pending"); // default status

            pst.executeUpdate();

            ResultSet rsKeys = pst.getGeneratedKeys();
            if(rsKeys.next()){
                int requestId = rsKeys.getInt(1);
                JOptionPane.showMessageDialog(this,"Request submitted successfully! ID: " + requestId);
            }

            loadRequests(sp);

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

    private void loadRequests(JScrollPane sp) {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
             PreparedStatement pst = con.prepareStatement("SELECT * FROM request_form_table WHERE org_id=?")) {

            pst.setInt(1, orgId);
            ResultSet rs = pst.executeQuery();

            boolean hasData = false;
            while(rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                        rs.getInt("request_id"),
                        rs.getString("blood_group"),
                        rs.getInt("quantity"),
                        rs.getDate("request_date"),
                        rs.getString("status")
                });
            }

            if(!hasData) JOptionPane.showMessageDialog(this,"No requests found!");
            sp.setVisible(true); // show table only after loading

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,"Error: "+e.getMessage());
        }
    }

}



