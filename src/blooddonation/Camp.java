package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Camp extends JFrame {
    private JTextField txtCampName, txtLocation, txtDate;
    private JButton btnAdd, btnSelect, btnUpdate, btnDelete, btnViewAll;
    private int selectedCampId = -1; // Stores selected camp ID for update/delete

    public Camp() {
        setTitle("Camp Management");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/admin_bg.jpeg")); // put your image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 500, 350);
        setContentPane(background);
        background.setLayout(null);

        // Labels + Fields
        JLabel lblName = new JLabel("Camp Name:");
        lblName.setBounds(30, 30, 100, 25);
        lblName.setForeground(java.awt.Color.BLACK);
        background.add(lblName);

        txtCampName = new JTextField();
        txtCampName.setBounds(140, 30, 200, 25);
        background.add(txtCampName);

        JLabel lblLoc = new JLabel("Location:");
        lblLoc.setBounds(30, 70, 100, 25);
        lblLoc.setForeground(java.awt.Color.BLACK);
        background.add(lblLoc);

        txtLocation = new JTextField();
        txtLocation.setBounds(140, 70, 200, 25);
        background.add(txtLocation);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setBounds(30, 110, 150, 25);
        lblDate.setForeground(java.awt.Color.BLACK);
        background.add(lblDate);

        txtDate = new JTextField();
        txtDate.setBounds(180, 110, 160, 25);
        background.add(txtDate);

        // Buttons
        btnAdd = new JButton("Add Camp");
        btnAdd.setBounds(30, 160, 120, 30);
        background.add(btnAdd);

        btnSelect = new JButton("Select Camp");
        btnSelect.setBounds(160, 160, 120, 30);
        background.add(btnSelect);

        btnUpdate = new JButton("Update Camp");
        btnUpdate.setBounds(290, 160, 140, 30);
        background.add(btnUpdate);

        btnDelete = new JButton("Delete Camp");
        btnDelete.setBounds(160, 200, 120, 30);
        background.add(btnDelete);

        btnViewAll = new JButton("View All");
        btnViewAll.setBounds(290, 200, 120, 30);
        background.add(btnViewAll);

        // Actions
        btnAdd.addActionListener(e -> { addCamp(); clearFields(); });
        btnSelect.addActionListener(e -> selectCamp());
        btnUpdate.addActionListener(e -> { updateCamp(); clearFields(); });
        btnDelete.addActionListener(e -> { deleteCamp(); clearFields(); });
        btnViewAll.addActionListener(e -> viewAllCamps());

        setVisible(true);
    }

    private void addCamp() {
        String sql = "INSERT INTO camp_table (camp_name, location, date) VALUES (?, ?, ?)";
        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtCampName.getText().trim());
            pst.setString(2, txtLocation.getText().trim());
            pst.setString(3, txtDate.getText().trim());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Camp added successfully!");
        } catch(Exception e){ System.out.println("Error adding camp: " + e); }
    }

    private void selectCamp() {
        String sql = "SELECT * FROM camp_table";
        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"CampID","Camp Name","Location","Date"},0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("camp_ID"),
                        rs.getString("camp_name"),
                        rs.getString("location"),
                        rs.getString("date")
                });
            }

            JTable table = new JTable(model);
            int selectedRow = JOptionPane.showConfirmDialog(this, new JScrollPane(table),
                    "Select Camp", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if(selectedRow == JOptionPane.OK_OPTION) {
                int row = table.getSelectedRow();
                if(row != -1) {
                    selectedCampId = (int) table.getValueAt(row,0);
                    txtCampName.setText((String)table.getValueAt(row,1));
                    txtLocation.setText((String)table.getValueAt(row,2));
                    txtDate.setText((String)table.getValueAt(row,3));
                }
            }

        } catch(Exception e){ System.out.println("Error selecting camp: " + e); }
    }

    private void updateCamp() {
        if(selectedCampId == -1) { JOptionPane.showMessageDialog(this,"Select a camp first!"); return; }
        String sql = "UPDATE camp_table SET camp_name=?, location=?, date=? WHERE camp_ID=?";
        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtCampName.getText().trim());
            pst.setString(2, txtLocation.getText().trim());
            pst.setString(3, txtDate.getText().trim());
            pst.setInt(4, selectedCampId);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this,"Camp updated successfully!");
            selectedCampId = -1;
        } catch(Exception e){ System.out.println("Error updating camp: " + e); }
    }

    private void deleteCamp() {
        if(selectedCampId == -1) { JOptionPane.showMessageDialog(this,"Select a camp first!"); return; }
        String sql = "DELETE FROM camp_table WHERE camp_ID=?";
        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, selectedCampId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this,"Camp deleted successfully!");
            selectedCampId = -1;
        } catch(Exception e){ System.out.println("Error deleting camp: " + e); }
    }

    private void viewAllCamps() {
        String sql = "SELECT * FROM camp_table";
        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"CampID","Camp Name","Location","Date"},0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("camp_ID"),
                        rs.getString("camp_name"),
                        rs.getString("location"),
                        rs.getString("date")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table),
                    "All Camps", JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception e){ System.out.println("Error viewing camps: " + e); }
    }

    private void clearFields() {
        txtCampName.setText(""); txtLocation.setText(""); txtDate.setText("");
        selectedCampId = -1;
    }

    public static void main(String[] args) {
        new Camp();
    }
}




