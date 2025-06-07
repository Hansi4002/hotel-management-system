package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderDetailsModel {
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

    public static List<String> getAllOrderIds() throws SQLException {
        List<String> orderIds = new ArrayList<>();
        String sql = "SELECT order_id FROM food_order";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orderIds.add(rs.getString("order_id"));
            }
        }
        return orderIds;
    }

    public static boolean updateFoodOrderDetail(FoodOrderDetailDTO dto) throws SQLException {
        String sql = "UPDATE Food_Order_Detail SET item_price = ?, quantity = ? WHERE menu_id = ? AND order_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDouble(1, (Double) dto.getItemPrice());
            stmt.setInt(2, dto.getQuantity());
            stmt.setString(3, dto.getMenuId());
            stmt.setString(4, dto.getOrderId());

            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean saveFoodOrderDetail(FoodOrderDetailDTO dto) throws SQLException {
        String sql = "INSERT INTO Food_Order_Detail (menu_id, order_id, item_price, quantity) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dto.getMenuId());
            stmt.setString(2, dto.getOrderId());
            stmt.setDouble(3, (Double) dto.getItemPrice());
            stmt.setInt(4, dto.getQuantity());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<FoodOrderDetailDTO> getAllFoodOrderDetails() throws SQLException {
        List<FoodOrderDetailDTO> details = new ArrayList<>();
        String sql = "SELECT * FROM Food_Order_Detail";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                details.add(new FoodOrderDetailDTO(
                        rs.getString("menu_id"),
                        rs.getString("order_id"),
                        rs.getDouble("item_price"),
                        rs.getInt("quantity")
                ));
            }
        }
        return details;
    }

    public boolean deleteFoodOrderDetail(String menuId, String orderId) throws SQLException {
        String sql = "DELETE FROM Food_Order_Detail WHERE menu_id = ? AND order_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, menuId);
            stmt.setString(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
}