package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.SupplierFoodDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierFoodModel {
    public static List<String> getAllMenuIds() throws SQLException {
        List<String> menuIds = new ArrayList<>();
        String sql = "SELECT menu_id FROM food";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                menuIds.add(rs.getString("menu_id"));
            }
        }
        return menuIds;
    }

    public static boolean updateSupplierFood(SupplierFoodDTO dto) throws SQLException {
        String sql = "UPDATE Supplier_Food SET cost=?, quantity=?, supply_date=? WHERE menu_id=? AND supplier_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setDouble(1, dto.getCost());
            stmt.setInt(2, dto.getQuantity());
            stmt.setDate(3, dto.getSupplyDate());
            stmt.setString(4, dto.getMenuId());
            stmt.setString(5, dto.getSupplierId());
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean saveSupplierFood(SupplierFoodDTO dto) throws SQLException {
        String sql = "INSERT INTO Supplier_Food (menu_id, supplier_id, cost, quantity, supply_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dto.getMenuId());
            stmt.setString(2, dto.getSupplierId());
            stmt.setDouble(3, dto.getCost());
            stmt.setInt(4, dto.getQuantity());
            stmt.setDate(5, dto.getSupplyDate());
            return stmt.executeUpdate() > 0;
        }
    }

    public static List<String> getAllSupplierIds() throws SQLException {
        List<String> supplierIds = new ArrayList<>();
        String sql = "SELECT supplier_id FROM supplier";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                supplierIds.add(rs.getString("supplier_id"));
            }
        }
        return supplierIds;
    }

    public boolean deleteSupplierFood(String menuId, String supplierId) throws SQLException {
        String sql = "DELETE FROM Supplier_Food WHERE menu_id = ? AND supplier_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, menuId);
            stmt.setString(2, supplierId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<SupplierFoodDTO> getAllSupplierFoods() throws SQLException {
        List<SupplierFoodDTO> details = new ArrayList<>();
        String sql = "SELECT * FROM Supplier_Food";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                details.add(new SupplierFoodDTO(
                        rs.getString("menu_id"),
                        rs.getString("supplier_id"),
                        rs.getDouble("cost"),
                        rs.getInt("quantity"),
                        rs.getDate("supply_date")
                ));
            }
        }
        return details;
    }
}
