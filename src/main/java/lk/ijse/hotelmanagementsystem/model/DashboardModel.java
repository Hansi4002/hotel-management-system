package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DashboardModel {
    public int getTotalRooms() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_rooms FROM room";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("total_rooms") : 0;
        }
    }

    public int getAvailableRooms() throws SQLException {
        String sql = "SELECT COUNT(*) AS available_rooms FROM room WHERE status = 'Available'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("available_rooms") : 0;
        }
    }

    public int getOccupiedRooms() throws SQLException {
        String sql = "SELECT COUNT(*) AS occupied_rooms FROM room WHERE status = 'Occupied'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("occupied_rooms") : 0;
        }
    }

    public int getMaintenanceRooms() throws SQLException {
        String sql = "SELECT COUNT(*) AS maintenance_rooms FROM room WHERE status = 'Maintenance'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("maintenance_rooms") : 0;
        }
    }

    public int getTotalReservations() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_bookings FROM reservation";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("total_bookings") : 0;
        }
    }

    public int getActiveReservations() throws SQLException {
        String sql = "SELECT COUNT(*) AS active_bookings FROM reservation WHERE check_out_date >= CURDATE()";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt("active_bookings") : 0;
        }
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total) AS total_revenue FROM reservation WHERE status = 'Paid'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getDouble("total_revenue") : 0.0;
        }
    }

    public double getTodayRevenue() throws SQLException {
        String sql = "SELECT SUM(total) AS today_revenue FROM reservation WHERE DATE(check_in_date) = CURDATE() AND status = 'Paid'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getDouble("today_revenue") : 0.0;
        }
    }

    public Map<String, Double> getMonthlyRevenue() throws SQLException {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        String sql = "SELECT DATE_FORMAT(check_in_date, '%Y-%m') AS month, SUM(total) AS revenue " +
                "FROM reservation WHERE status = 'Paid' " +
                "GROUP BY DATE_FORMAT(check_in_date, '%Y-%m') " +
                "ORDER BY month DESC LIMIT 12";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                monthlyRevenue.put(rs.getString("month"), rs.getDouble("revenue"));
            }
        }
        return monthlyRevenue;
    }

    public Map<String, Integer> getDailyBookings() throws SQLException {
        Map<String, Integer> dailyBookings = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailyBookings.put(date.format(DateTimeFormatter.ISO_LOCAL_DATE), 0);
        }

        String sql = "SELECT DATE(created_date) AS booking_date, COUNT(*) AS bookings " +
                "FROM reservation " +
                "WHERE DATE(created_date) BETWEEN ? AND ? " +
                "GROUP BY DATE(created_date)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, startDate.toString());
            pstm.setString(2, endDate.toString());
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                dailyBookings.put(rs.getString("booking_date"), rs.getInt("bookings"));
            }
        }
        return dailyBookings;
    }
}