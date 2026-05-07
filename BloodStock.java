package blooddonation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BloodStock extends JFrame implements ActionListener {

    private JComboBox<String> campIdBox;
    private JTextField bloodGroupField;
    private JTextField quantityField;
    private JButton addBtn, updateBtn, deleteBtn, selectBtn, viewBtn;

    private int selectedStockId = -1;

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/login_schema";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Aswani@14N";

    public BloodStock() {
        setTitle("Blood Stock Management");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/red.jpg"));
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 700, 400);
        setContentPane(background);
        background.setLayout(null);

        // Labels and fields
        JLabel lblCamp = new JLabel("Camp:");
        lblCamp.setBounds(150, 100, 100, 25);
        background.add(lblCamp);

        campIdBox = new JComboBox<>();
        campIdBox.setBounds(250, 100, 200, 25);
        background.add(campIdBox);

        JLabel lblBG = new JLabel("Blood Group:");
        lblBG.setBounds(150, 150, 100, 25);
        background.add(lblBG);

        bloodGroupField = new JTextField();
        bloodGroupField.setBounds(250, 150, 200, 25);
        background.add(bloodGroupField);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setBounds(150, 200, 100, 25);
        background.add(lblQty);

        quantityField = new JTextField();
        quantityField.setBounds(250, 200, 200, 25);
        background.add(quantityField);

        // Buttons
        addBtn = new JButton("Add");
        addBtn.setBounds(120, 250, 100, 30);
        background.add(addBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(280, 250, 100, 30);
        background.add(updateBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(420, 250, 100, 30);
        background.add(deleteBtn);

        selectBtn = new JButton("Select");
        selectBtn.setBounds(200, 300, 100, 30);
        background.add(selectBtn);

        viewBtn = new JButton("View");
        viewBtn.setBounds(350, 300, 100, 30);
        background.add(viewBtn);

        // Load camps
        loadCampIds();

        // Add action listeners
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        selectBtn.addActionListener(this);
        viewBtn.addActionListener(this);

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void loadCampIds() {
        campIdBox.removeAllItems();
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT camp_id, camp_name FROM camp_table")) {

            while (rs.next()) {
                campIdBox.addItem(rs.getInt("camp_id") + " - " + rs.getString("camp_name"));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading camps: " + ex.getMessage());
        }
    }

    private int getSelectedCampId() {
        String sel = (String) campIdBox.getSelectedItem();
        return Integer.parseInt(sel.split(" - ")[0]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == addBtn) addStock();
        else if (src == updateBtn) updateStock();
        else if (src == deleteBtn) deleteStock();
        else if (src == selectBtn) selectStock();
        else if (src == viewBtn) viewStock();
    }

    private void addStock() {
        String bg = bloodGroupField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        if (bg.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "INSERT INTO blood_stock_table (camp_id, blood_group, quantity) VALUES (?,?,?)")) {

            pst.setInt(1, getSelectedCampId());
            pst.setString(2, bg);
            pst.setInt(3, Integer.parseInt(qtyStr));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock added.");
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding stock: " + e.getMessage());
        }
    }

    private void updateStock() {
        if (selectedStockId == -1) {
            JOptionPane.showMessageDialog(this, "Select a stock first!");
            return;
        }
        String bg = bloodGroupField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        if (bg.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "UPDATE blood_stock_table SET camp_id=?, blood_group=?, quantity=? WHERE stock_id=?")) {

            pst.setInt(1, getSelectedCampId());
            pst.setString(2, bg);
            pst.setInt(3, Integer.parseInt(qtyStr));
            pst.setInt(4, selectedStockId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock updated.");
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating stock: " + e.getMessage());
        }
    }

    private void deleteStock() {
        if (selectedStockId == -1) {
            JOptionPane.showMessageDialog(this, "Select a stock first!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected stock?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement("DELETE FROM blood_stock_table WHERE stock_id=?")) {

            pst.setInt(1, selectedStockId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock deleted.");
            clearFields();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting stock: " + e.getMessage());
        }
    }

    private void selectStock() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Stock ID", "Camp ID", "Camp Name", "Blood Group", "Quantity"}, 0);

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT b.stock_id, b.camp_id, c.camp_name, b.blood_group, b.quantity " +
                             "FROM blood_stock_table b LEFT JOIN camp_table c ON b.camp_id=c.camp_id")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("stock_id"),
                        rs.getInt("camp_id"),
                        rs.getString("camp_name"),
                        rs.getString("blood_group"),
                        rs.getInt("quantity")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stocks: " + e.getMessage());
            return;
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int option = JOptionPane.showConfirmDialog(this, new JScrollPane(table),
                "Select Stock", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION && table.getSelectedRow() != -1) {
            selectedStockId = (int) table.getValueAt(table.getSelectedRow(), 0);
            int campId = (int) table.getValueAt(table.getSelectedRow(), 1);
            String campName = (String) table.getValueAt(table.getSelectedRow(), 2);
            campIdBox.setSelectedItem(campId + " - " + campName);
            bloodGroupField.setText((String) table.getValueAt(table.getSelectedRow(), 3));
            quantityField.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        }
    }

    private void viewStock() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Stock ID", "Camp ID", "Camp Name", "Blood Group", "Quantity"}, 0);

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT b.stock_id, b.camp_id, c.camp_name, b.blood_group, b.quantity " +
                             "FROM blood_stock_table b LEFT JOIN camp_table c ON b.camp_id=c.camp_id")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("stock_id"),
                        rs.getInt("camp_id"),
                        rs.getString("camp_name"),
                        rs.getString("blood_group"),
                        rs.getInt("quantity")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading stocks: " + e.getMessage());
            return;
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(table), "All Blood Stock", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        selectedStockId = -1;
        bloodGroupField.setText("");
        quantityField.setText("");
        if (campIdBox.getItemCount() > 0) campIdBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodStock());
    }
}







