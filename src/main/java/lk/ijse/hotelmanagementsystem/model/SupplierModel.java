package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.SupplierDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {
    public static String getNextSupplierId() throws SQLException {
        String sql = "SELECT supplier_id FROM supplier ORDER BY supplier_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(3));
                int nextId = lastIdNumberInt + 1;
                return String.format("SUP%03d", nextId);
            } else {
                return "SUP001";
            }
        }
    }

    public List<SupplierDTO> getAllSuppliers() throws SQLException {
        List<SupplierDTO> supplierList = new ArrayList<>();
        String sql = "SELECT * FROM supplier";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SupplierDTO supplier = new SupplierDTO(
                        rs.getString("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getString("address")
                );
                supplierList.add(supplier);
            }
        }catch (SQLException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
            return supplierList;
        }
        return supplierList;
    }

    public boolean deleteSupplier(String supplierId) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, supplierId);
            return stm.executeUpdate() > 0;
        }
    }

    public boolean updateSupplier(SupplierDTO dto) throws SQLException {
        String sql = "UPDATE supplier SET name=? , contact=? , email=? , address=? WHERE  supplier_id =?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getName());
            stm.setString(2, dto.getContact());
            stm.setString(3, dto.getEmail());
            stm.setString(4, dto.getAddress());
            stm.setString(5, dto.getSupplierId());
            return stm.executeUpdate() > 0;
        }
    }

    public boolean saveSupplier(SupplierDTO dto) throws SQLException {
        String sql = "INSERT INTO supplier (supplier_id, name, contact, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getSupplierId());
            stm.setString(2, dto.getName());
            stm.setString(3, dto.getContact());
            stm.setString(4, dto.getEmail());
            stm.setString(5, dto.getAddress());

            return stm.executeUpdate() > 0;
        }
    }
}