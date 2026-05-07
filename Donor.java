package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Donor extends JFrame {
    private JTextField tfName, tfAge, tfPhone, tfLastDate;
    private JComboBox<String> cbGender, cbBG, cbCamp;
    private int selectedDonorId = -1;

    public Donor() {
        setTitle("Donor Management");
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/world.jpg")); // put your image in src/images
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 500, 350);
        setContentPane(background);
        background.setLayout(null);;

        // Labels and fields
        JLabel lblName = new JLabel("Name");
        lblName.setForeground(Color.BLACK);
        lblName.setBounds(10, 10, 80, 25);
        background.add(lblName);

        tfName = new JTextField();
        tfName.setBounds(90, 10, 200, 25);
        background.add(tfName);

        JLabel lblAge = new JLabel("Age");
        lblAge.setForeground(Color.BLACK);
        lblAge.setBounds(300, 10, 40, 25);
        background.add(lblAge);

        tfAge = new JTextField();
        tfAge.setBounds(340, 10, 40, 25);
        background.add(tfAge);

        JLabel lblGender = new JLabel("Gender");
        lblGender.setForeground(Color.BLACK);
        lblGender.setBounds(400, 10, 60, 25);
        background.add(lblGender);

        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setBounds(460, 10, 90, 25);
        background.add(cbGender);

        JLabel lblBG = new JLabel("Blood Group");
        lblBG.setForeground(Color.BLACK);
        lblBG.setBounds(560, 10, 90, 25);
        background.add(lblBG);

        cbBG = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        cbBG.setBounds(650, 10, 80, 25);
        background.add(cbBG);

        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setForeground(Color.BLACK);
        lblPhone.setBounds(10, 50, 60, 25);
        background.add(lblPhone);

        tfPhone = new JTextField();
        tfPhone.setBounds(70, 50, 120, 25);
        background.add(tfPhone);

        JLabel lblLastDate = new JLabel("Last Donation (YYYY-MM-DD)");
        lblLastDate.setForeground(Color.BLACK);
        lblLastDate.setBounds(200, 50, 180, 25);
        background.add(lblLastDate);

        tfLastDate = new JTextField();
        tfLastDate.setBounds(390, 50, 120, 25);
        background.add(tfLastDate);

        JLabel lblCamp = new JLabel("Camp");
        lblCamp.setForeground(Color.BLACK);
        lblCamp.setBounds(520, 50, 50, 25);
        background.add(lblCamp);

        cbCamp = new JComboBox<>();
        cbCamp.setBounds(580, 50, 150, 25);
        background.add(cbCamp);

        // Buttons
        JButton btnAdd = new JButton("Add"); btnAdd.setBounds(10, 120, 100, 30); background.add(btnAdd);
        JButton btnSelect = new JButton("Select for Update/Delete"); btnSelect.setBounds(120, 120, 200, 30); background.add(btnSelect);
        JButton btnUpdate = new JButton("Update"); btnUpdate.setBounds(350, 120, 100, 30); background.add(btnUpdate);
        JButton btnDelete = new JButton("Delete"); btnDelete.setBounds(490, 120, 100, 30); background.add(btnDelete);
        JButton btnViewAll = new JButton("View All"); btnViewAll.setBounds(610, 120, 120, 30); background.add(btnViewAll);

        // Add background to frame
        setContentPane(background);

        // Load camp dropdown
        loadCamps();

        // Button actions
        btnAdd.addActionListener(e -> { addDonor(); clearFields(); });
        btnSelect.addActionListener(e -> selectDonor());
        btnUpdate.addActionListener(e -> { updateDonor(); clearFields(); });
        btnDelete.addActionListener(e -> { deleteDonor(); clearFields(); });
        btnViewAll.addActionListener(e -> viewAllDonors());

        setVisible(true);
    }
    // 🔹 Keep your existing DB methods (loadCamps, addDonor, selectDonor, updateDonor, deleteDonor, adjustStockAfterDonation, viewAllDonors, clearFields)
    // They remain unchanged



private void loadCamps() {
        cbCamp.removeAllItems();
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement("SELECT camp_id, camp_name FROM camp_table");
            ResultSet rs = pst.executeQuery()) {
            while(rs.next()) {
                cbCamp.addItem(rs.getInt("camp_id") + " - " + rs.getString("camp_name"));
            }
        } catch(Exception e){ System.out.println("Error loading camps: " + e); }
    }

    private void addDonor() {
        String name = tfName.getText().trim();
        if(name.isEmpty()) { JOptionPane.showMessageDialog(this,"Enter donor name"); return; }
        String campSel = (String) cbCamp.getSelectedItem();
        Integer campId = null;
        if(campSel != null) campId = Integer.parseInt(campSel.split(" - ")[0]);

        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO donor_table (camp_id, donor_name, age, gender, blood_group, phone, last_donation_date) VALUES (?,?,?,?,?,?,?)")) {

            if(campId == null) pst.setNull(1, java.sql.Types.INTEGER); else pst.setInt(1, campId);
            pst.setString(2, name);
            pst.setInt(3, tfAge.getText().trim().isEmpty()?0:Integer.parseInt(tfAge.getText().trim()));
            pst.setString(4, (String)cbGender.getSelectedItem());
            pst.setString(5, (String)cbBG.getSelectedItem());
            pst.setString(6, tfPhone.getText().trim());
            pst.setString(7, tfLastDate.getText().trim().isEmpty()?null:tfLastDate.getText().trim());
            pst.executeUpdate();

            if(campId != null) adjustStockAfterDonation(campId, (String)cbBG.getSelectedItem());

            JOptionPane.showMessageDialog(this,"Donor added successfully!");
        } catch(Exception e){ System.out.println("Error adding donor: " + e); }
    }

    private void selectDonor() {
        String sql = "SELECT d.*, c.camp_name FROM donor_table d LEFT JOIN camp_table c ON d.camp_id=c.camp_id";
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"DonorID","Name","Age","Gender","BloodGroup","Phone","LastDonation","Camp"},0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("donor_id"), rs.getString("donor_name"), rs.getInt("age"),
                        rs.getString("gender"), rs.getString("blood_group"),
                        rs.getString("phone"), rs.getString("last_donation_date"),
                        rs.getString("camp_name")==null?"None":rs.getString("camp_name")
                });
            }

            JTable table = new JTable(model);
            int selectedRow = JOptionPane.showConfirmDialog(this, new JScrollPane(table), "Select Donor",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // Get selected row
            if(selectedRow == JOptionPane.OK_OPTION) {
                selectedDonorId = (int) table.getValueAt(table.getSelectedRow(),0);
                tfName.setText((String)table.getValueAt(table.getSelectedRow(),1));
                tfAge.setText(String.valueOf(table.getValueAt(table.getSelectedRow(),2)));
                cbGender.setSelectedItem(table.getValueAt(table.getSelectedRow(),3));
                cbBG.setSelectedItem(table.getValueAt(table.getSelectedRow(),4));
                tfPhone.setText((String)table.getValueAt(table.getSelectedRow(),5));
                tfLastDate.setText((String)table.getValueAt(table.getSelectedRow(),6));
                String campName = (String)table.getValueAt(table.getSelectedRow(),7);
                if(!campName.equals("None")) cbCamp.setSelectedItem(table.getValueAt(table.getSelectedRow(),7));
            }

        } catch(Exception e){ System.out.println("Error selecting donor: " + e); }
    }

    private void updateDonor() {
        if(selectedDonorId == -1) { JOptionPane.showMessageDialog(this,"Select a donor first!"); return; }

        String campSel = (String) cbCamp.getSelectedItem();
        Integer campId = campSel==null?null:Integer.parseInt(campSel.split(" - ")[0]);

        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE donor_table SET camp_id=?, donor_name=?, age=?, gender=?, blood_group=?, phone=?, last_donation_date=? WHERE donor_id=?")) {

            if(campId==null) pst.setNull(1, java.sql.Types.INTEGER); else pst.setInt(1, campId);
            pst.setString(2, tfName.getText().trim());
            pst.setInt(3, tfAge.getText().trim().isEmpty()?0:Integer.parseInt(tfAge.getText().trim()));
            pst.setString(4, (String)cbGender.getSelectedItem());
            pst.setString(5, (String)cbBG.getSelectedItem());
            pst.setString(6, tfPhone.getText().trim());
            pst.setString(7, tfLastDate.getText().trim().isEmpty()?null:tfLastDate.getText().trim());
            pst.setInt(8, selectedDonorId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,"Donor updated successfully!");
            selectedDonorId = -1;
        } catch(Exception e){ System.out.println("Error updating donor: " + e); }
    }

    private void deleteDonor() {
        if(selectedDonorId == -1) { JOptionPane.showMessageDialog(this,"Select a donor first!"); return; }
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement("DELETE FROM donor_table WHERE donor_id=?")) {
            pst.setInt(1, selectedDonorId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this,"Donor deleted successfully!");
            selectedDonorId = -1;
        } catch(Exception e){ System.out.println("Error deleting donor: " + e); }
    }

    private void adjustStockAfterDonation(int campId, String bloodGroup) {
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement("SELECT * FROM bloodstock_table WHERE camp_id=? AND blood_group=?")) {
            pst.setInt(1, campId); pst.setString(2, bloodGroup);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                PreparedStatement up = con.prepareStatement("UPDATE bloodstock_table SET quantity=quantity+1 WHERE stock_id=?");
                up.setInt(1, rs.getInt("stock_id"));
                up.executeUpdate();
            } else {
                PreparedStatement in = con.prepareStatement("INSERT INTO bloodstock_table (camp_id, blood_group, quantity) VALUES (?,?,1)");
                in.setInt(1,campId); in.setString(2,bloodGroup); in.executeUpdate();
            }
        } catch(Exception e){ System.out.println("Error updating stock: " + e); }
    }

    private void viewAllDonors() {
        String sql = "SELECT d.*, c.camp_name FROM donor_table d LEFT JOIN camp_table c ON d.camp_id=c.camp_id";
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/login_schema","root","Aswani@14N");
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"DonorID","Name","Age","Gender","BloodGroup","Phone","LastDonation","Camp"},0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("donor_id"), rs.getString("donor_name"), rs.getInt("age"),
                        rs.getString("gender"), rs.getString("blood_group"),
                        rs.getString("phone"), rs.getString("last_donation_date"),
                        rs.getString("camp_name")==null?"None":rs.getString("camp_name")
                });
            }
            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table), "All Donors", JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception e){ System.out.println("Error loading donors: " + e); }
    }

    private void clearFields() {
        tfName.setText(""); tfAge.setText(""); tfPhone.setText(""); tfLastDate.setText("");
        cbGender.setSelectedIndex(0); cbBG.setSelectedIndex(0); cbCamp.setSelectedIndex(0);
        selectedDonorId = -1;
    }

    public static void main(String[] args) {
        new Donor();
    }
}










