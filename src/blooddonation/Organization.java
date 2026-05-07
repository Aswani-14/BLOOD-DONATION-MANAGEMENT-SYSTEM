package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Organization extends JFrame {
    private JTextField txtOrgID, txtOrgName, txtLoginUsername, txtLoginPassword;
    private JButton btnAdd, btnSelect, btnUpdate, btnDelete, btnViewAll;

    public Organization() {
        setTitle("Organization Management");
        setSize(500,400); // bigger to fit background nicely
        setLocationRelativeTo(null);
        setLayout(null);

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/report.jpg")); // put image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 500, 400);
        setContentPane(background);
        background.setLayout(null);

        // Labels & Fields
        JLabel lblOrgID = new JLabel("Org ID:"); lblOrgID.setBounds(30,30,100,25); background.add(lblOrgID);
        txtOrgID = new JTextField(); txtOrgID.setBounds(150,30,200,25); txtOrgID.setEditable(false); background.add(txtOrgID);

        JLabel lblOrgName = new JLabel("Org Name:"); lblOrgName.setBounds(30,70,100,25); background.add(lblOrgName);
        txtOrgName = new JTextField(); txtOrgName.setBounds(150,70,200,25); background.add(txtOrgName);

        JLabel lblLoginUsername = new JLabel("Login Username:"); lblLoginUsername.setBounds(30,110,120,25); background.add(lblLoginUsername);
        txtLoginUsername = new JTextField(); txtLoginUsername.setBounds(150,110,200,25); background.add(txtLoginUsername);

        JLabel lblLoginPassword = new JLabel("Login Password:"); lblLoginPassword.setBounds(30,150,120,25); background.add(lblLoginPassword);
        txtLoginPassword = new JTextField(); txtLoginPassword.setBounds(150,150,200,25); background.add(txtLoginPassword);

        // Buttons
        btnAdd = new JButton("Add"); btnAdd.setBounds(30,200,80,30); background.add(btnAdd);
        btnSelect = new JButton("Select"); btnSelect.setBounds(120,200,80,30); background.add(btnSelect);
        btnUpdate = new JButton("Update"); btnUpdate.setBounds(210,200,80,30); background.add(btnUpdate);
        btnDelete = new JButton("Delete"); btnDelete.setBounds(300,200,80,30); background.add(btnDelete);
        btnViewAll = new JButton("View All"); btnViewAll.setBounds(150,250,100,30); background.add(btnViewAll);

        // Button actions
        btnAdd.addActionListener(e -> { addOrganization(); clearFields(); });
        btnSelect.addActionListener(e -> selectOrganization());
        btnUpdate.addActionListener(e -> { updateOrganization(); clearFields(); });
        btnDelete.addActionListener(e -> { deleteOrganization(); clearFields(); });
        btnViewAll.addActionListener(e -> viewAllOrganizations());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // All your previous methods (addOrganization, selectOrganization, updateOrganization, deleteOrganization, viewAllOrganizations, clearFields)
    // can remain exactly the same.

    private void addOrganization() {
        String username = txtLoginUsername.getText().trim();
        String password = txtLoginPassword.getText().trim();
        String orgName = txtOrgName.getText().trim();
        if(username.isEmpty() || password.isEmpty() || orgName.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Fill all fields");
            return;
        }

        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N")) {
            PreparedStatement pstLogin = con.prepareStatement(
                    "INSERT INTO login_table (username, password, role) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstLogin.setString(1, username);
            pstLogin.setString(2, password);
            pstLogin.setString(3, "organization");
            pstLogin.executeUpdate();

            ResultSet rs = pstLogin.getGeneratedKeys();
            int loginId = -1;
            if(rs.next()) loginId = rs.getInt(1);

            PreparedStatement pstOrg = con.prepareStatement(
                    "INSERT INTO organization_table (OrgName, login_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            pstOrg.setString(1, orgName);
            pstOrg.setInt(2, loginId);
            pstOrg.executeUpdate();

            rs = pstOrg.getGeneratedKeys();
            if(rs.next()) txtOrgID.setText(String.valueOf(rs.getInt(1)));

            JOptionPane.showMessageDialog(this,"Organization added successfully!");
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void selectOrganization() {
        String sql = "SELECT o.org_id, o.OrgName, l.username, l.password " +
                "FROM organization_table o JOIN login_table l ON o.login_id=l.login_id";
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"OrgID","OrgName","Username","Password"},0);
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("org_id"),
                        rs.getString("OrgName"),
                        rs.getString("username"),
                        rs.getString("password")
                });
            }

            JTable table = new JTable(model);
            table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

            int option = JOptionPane.showConfirmDialog(this, new JScrollPane(table),
                    "Select Organization", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION){
                int selectedRow = table.getSelectedRow();
                if(selectedRow != -1){
                    txtOrgID.setText(model.getValueAt(selectedRow,0).toString());
                    txtOrgName.setText(model.getValueAt(selectedRow,1).toString());
                    txtLoginUsername.setText(model.getValueAt(selectedRow,2).toString());
                    txtLoginPassword.setText(model.getValueAt(selectedRow,3).toString());
                }
            }

        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void updateOrganization() {
        if(txtOrgID.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this,"Select organization first!"); return; }
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N")) {
            PreparedStatement pstGetLogin = con.prepareStatement("SELECT login_id FROM organization_table WHERE org_id=?");
            pstGetLogin.setInt(1, Integer.parseInt(txtOrgID.getText()));
            ResultSet rs = pstGetLogin.executeQuery();
            if(rs.next()) {
                int loginId = rs.getInt("login_id");

                PreparedStatement pstLogin = con.prepareStatement("UPDATE login_table SET username=?, password=? WHERE login_id=?");
                pstLogin.setString(1, txtLoginUsername.getText());
                pstLogin.setString(2, txtLoginPassword.getText());
                pstLogin.setInt(3, loginId);
                pstLogin.executeUpdate();

                PreparedStatement pstOrg = con.prepareStatement("UPDATE organization_table SET OrgName=? WHERE org_id=?");
                pstOrg.setString(1, txtOrgName.getText());
                pstOrg.setInt(2, Integer.parseInt(txtOrgID.getText()));
                pstOrg.executeUpdate();

                JOptionPane.showMessageDialog(this,"Organization updated successfully!");
            }

        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void deleteOrganization() {
        if(txtOrgID.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this,"Select organization first!"); return; }
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N")) {
            PreparedStatement pstGetLogin = con.prepareStatement("SELECT login_id FROM organization_table WHERE org_id=?");
            pstGetLogin.setInt(1, Integer.parseInt(txtOrgID.getText()));
            ResultSet rs = pstGetLogin.executeQuery();
            if(rs.next()) {
                int loginId = rs.getInt("login_id");

                PreparedStatement pstOrg = con.prepareStatement("DELETE FROM organization_table WHERE org_id=?");
                pstOrg.setInt(1, Integer.parseInt(txtOrgID.getText()));
                pstOrg.executeUpdate();

                PreparedStatement pstLogin = con.prepareStatement("DELETE FROM login_table WHERE login_id=?");
                pstLogin.setInt(1, loginId);
                pstLogin.executeUpdate();

                JOptionPane.showMessageDialog(this,"Organization deleted successfully!");
            }

        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void viewAllOrganizations() {
        String sql = "SELECT o.org_id, o.OrgName, l.username, l.password " +
                "FROM organization_table o JOIN login_table l ON o.login_id=l.login_id";
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"OrgID","Org Name","Username","Password"},0);
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("org_id"),
                        rs.getString("OrgName"),
                        rs.getString("username"),
                        rs.getString("password")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this,new JScrollPane(table),"All Organizations",JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void clearFields() {
        txtOrgID.setText(""); txtOrgName.setText(""); txtLoginUsername.setText(""); txtLoginPassword.setText("");
    }

    public static void main(String[] args){ SwingUtilities.invokeLater(Organization::new); }
}





