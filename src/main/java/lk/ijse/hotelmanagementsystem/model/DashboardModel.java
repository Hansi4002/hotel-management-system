package lk.ijse.hotelmanagementsystem.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardModel {

    private static final String URL = "jdbc:mysql://localhost:3306/HotelManagementSystem";
    private static final String USER = "root";
    private static final String PASSWORD = "mySQL";

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database: " + e.getMessage(), e);
        }
    }

    public int getTotalRooms() throws SQLException {
        String query = "SELECT COUNT(*) FROM Room";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new SQLException("Error fetching total rooms: " + e.getMessage(), e);
        }
    }

    public int getTotalReservations() throws SQLException {
        String query = "SELECT COUNT(*) FROM Reservation WHERE ? BETWEEN check_in_date AND check_out_date";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching total reservations: " + e.getMessage(), e);
        }
    }

    public long getTotalRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payment WHERE status = 'Completed'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } catch (SQLException e) {
            throw new SQLException("Error fetching total revenue: " + e.getMessage(), e);
        }
    }
}