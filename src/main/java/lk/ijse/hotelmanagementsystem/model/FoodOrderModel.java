package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderModel {
    public static List<String> getAllReservationIds() throws SQLException {
        List<String> reservationIds = new ArrayList<>();
        String sql = "SELECT reservation_id FROM Reservation ORDER BY reservation_id DESC";
        try(Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs =stmt.executeQuery()){
            while (rs.next()){
                reservationIds.add(rs.getString("reservation_id"));
            }
        }
        return reservationIds;
    }

    public static String getNextOrderId() throws SQLException {
        String sql = "SELECT order_id FROM Food_Order ORDER BY order_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumber = Integer.parseInt(lastId.substring(1));
                int nextId = lastIdNumber + 1;
                return String.format("O%03d", nextId);
            } else {
                return "O001";
            }
        }
    }

    public static boolean updateFoodOrder(FoodOrderDTO foodOrderDTO) throws SQLException {
        String sql = "UPDATE Food_Order SET reservation_id = ?, order_type = ?, status = ?, total_amount = ?, order_date = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, foodOrderDTO.getReservationId());
            stmt.setString(2, foodOrderDTO.getOrderType());
            stmt.setString(3, foodOrderDTO.getStatus());
            stmt.setDouble(4, foodOrderDTO.getTotalAmount());
            stmt.setDate(5, foodOrderDTO.getOrderDate());
            stmt.setString(6, foodOrderDTO.getOrderId());
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean saveFoodOrder(FoodOrderDTO foodOrderDTO) throws SQLException {
        String sql = "INSERT INTO Food_Order (order_id, reservation_id, order_type, status, total_amount, order_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, foodOrderDTO.getOrderId());
            stmt.setString(2, foodOrderDTO.getReservationId());
            stmt.setString(3, foodOrderDTO.getOrderType());
            stmt.setString(4, foodOrderDTO.getStatus());
            stmt.setDouble(5, foodOrderDTO.getTotalAmount());
            stmt.setDate(6, foodOrderDTO.getOrderDate());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<FoodOrderDTO> getAllFoodOrders() throws SQLException {
        List<FoodOrderDTO> foodOrderList = new ArrayList<>();
        String sql = "SELECT * FROM Food_Order";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                FoodOrderDTO foodOrder = new FoodOrderDTO(
                        rs.getString("order_id"),
                        rs.getString("reservation_id"),
                        rs.getString("order_type"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("order_date").toLocalDateTime()
                );
                foodOrderList.add(foodOrder);
            }
        }
        return foodOrderList;
    }

    public boolean cancelFoodOrder(String orderId) {
        String sql = "UPDATE Food_Order SET status = 'CANCELLED' WHERE order_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
