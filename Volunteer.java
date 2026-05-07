package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Volunteer extends JFrame {
    private JComboBox<String> cmbCampId;
    private JTextField txtVolunteerID, txtName, txtContact;
    private JButton btnAdd, btnSelect, btnUpdate, btnDelete, btnViewAll;
    private Integer selectedVolunteerId = null;

    public Volunteer() {
        setTitle("Volunteer Management");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // === BACKGROUND IMAGE ===
        ImageIcon bgImage = new ImageIcon(getClass().getResource("/images/volunteer.jpeg"));
        JLabel background = new JLabel(bgImage);
        background.setBounds(0, 0, 600, 450);
        setContentPane(background);
        setLayout(null);

        // === Labels & Fields ===
        JLabel lblVolunteerID = new JLabel("Volunteer ID:");
        lblVolunteerID.setBounds(30, 30, 100, 25);
        add(lblVolunteerID);

        txtVolunteerID = new JTextField();
        txtVolunteerID.setBounds(150, 30, 200, 25);
        txtVolunteerID.setEditable(false);
        add(txtVolunteerID);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(30, 70, 100, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(150, 70, 200, 25);
        add(txtName);

        JLabel lblContact = new JLabel("Contact:");
        lblContact.setBounds(30, 110, 100, 25);
        add(lblContact);

        txtContact = new JTextField();
        txtContact.setBounds(150, 110, 200, 25);
        add(txtContact);

        JLabel lblCamp = new JLabel("Camp:");
        lblCamp.setBounds(30, 150, 100, 25);
        add(lblCamp);

        cmbCampId = new JComboBox<>();
        cmbCampId.setBounds(150, 150, 200, 25);
        add(cmbCampId);

        // === Buttons ===
        btnAdd = new JButton("Add");
        btnAdd.setBounds(30, 200, 100, 30);
        add(btnAdd);

        btnSelect = new JButton("Select");
        btnSelect.setBounds(150, 200, 100, 30);
        add(btnSelect);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(270, 200, 100, 30);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(390, 200, 100, 30);
        add(btnDelete);

        btnViewAll = new JButton("View All");
        btnViewAll.setBounds(180, 250, 120, 30);
        add(btnViewAll);

        // Load camp IDs into combo box
        loadCampIds();

        // Actions
        btnAdd.addActionListener(e -> { addVolunteer(); clearFields(); });
        btnSelect.addActionListener(e -> selectVolunteer());
        btnUpdate.addActionListener(e -> { updateVolunteer(); clearFields(); });
        btnDelete.addActionListener(e -> { deleteVolunteer(); clearFields(); });
        btnViewAll.addActionListener(e -> viewAllVolunteers());

        setVisible(true);
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/login_schema", "root", "Aswani@14N");
    }

    private void loadCampIds() {
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT camp_id, camp_name FROM camp_table")) {

            cmbCampId.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("camp_id");
                String name = rs.getString("camp_name");
                cmbCampId.addItem(id + " - " + name);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading camps: " + e.getMessage());
        }
    }

    private void addVolunteer() {
        String sql = "INSERT INTO volunteer_table (volunteer_name, phone, camp_id) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, txtName.getText());
            pst.setString(2, txtContact.getText());
            pst.setInt(3, Integer.parseInt(cmbCampId.getSelectedItem().toString().split(" - ")[0]));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    txtVolunteerID.setText(String.valueOf(rs.getInt(1)));
                }
                JOptionPane.showMessageDialog(this, "Volunteer added successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding volunteer: " + e.getMessage());
        }
    }

    private void selectVolunteer() {
        String sql = "SELECT * FROM volunteer_table";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Volunteer ID", "Name", "Contact", "Camp ID"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteer_name"),
                        rs.getString("phone"),
                        rs.getInt("camp_id")
                });
            }

            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            int option = JOptionPane.showConfirmDialog(this, new JScrollPane(table),
                    "Select Volunteer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedVolunteerId = (Integer) table.getValueAt(selectedRow, 0);
                    txtVolunteerID.setText(String.valueOf(selectedVolunteerId));
                    txtName.setText((String) table.getValueAt(selectedRow, 1));
                    txtContact.setText((String) table.getValueAt(selectedRow, 2));
                    cmbCampId.setSelectedItem(table.getValueAt(selectedRow, 3) + " - ");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error selecting volunteer: " + e.getMessage());
        }
    }

    private void updateVolunteer() {
        if (txtVolunteerID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a volunteer first!");
            return;
        }
        String sql = "UPDATE volunteer_table SET volunteer_name=?, phone=?, camp_id=? WHERE volunteer_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtName.getText());
            pst.setString(2, txtContact.getText());
            pst.setInt(3, Integer.parseInt(cmbCampId.getSelectedItem().toString().split(" - ")[0]));
            pst.setInt(4, Integer.parseInt(txtVolunteerID.getText()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Volunteer updated successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating volunteer: " + e.getMessage());
        }
    }

    private void deleteVolunteer() {
        if (txtVolunteerID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a volunteer first!");
            return;
        }
        String sql = "DELETE FROM volunteer_table WHERE volunteer_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(txtVolunteerID.getText()));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Volunteer deleted successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting volunteer: " + e.getMessage());
        }
    }

    private void viewAllVolunteers() {
        String sql = "SELECT v.volunteer_id, v.volunteer_name, v.phone, c.camp_name " +
                "FROM volunteer_table v JOIN camp_table c ON v.camp_id = c.camp_id";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Volunteer ID", "Name", "Contact", "Camp"}, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteer_name"),
                        rs.getString("phone"),
                        rs.getString("camp_name")
                });
            }

            JTable table = new JTable(model);
            JOptionPane.showMessageDialog(this, new JScrollPane(table),
                    "All Volunteers", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error viewing volunteers: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtVolunteerID.setText("");
        txtName.setText("");
        txtContact.setText("");
        if (cmbCampId.getItemCount() > 0) cmbCampId.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        new Volunteer();
    }
}


