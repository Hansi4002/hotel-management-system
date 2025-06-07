package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.MaintenanceDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.MaintenanceTM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceModel {

    public static String generateMaintenanceId() throws SQLException {
        String sql = "SELECT maintenance_id FROM Maintenance ORDER BY maintenance_id DESC LIMIT 1";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("maintenance_id");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                if (num > 999) {
                    throw new SQLException("Cannot generate maintenance_id: Exceeds maximum value (M999)");
                }
                String newId = String.format("M%03d", num);
                return newId;
            } else {
                return "M001";
            }
        }
    }

    public static List<String> getAllStaffIds() throws SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = "SELECT staff_id FROM Staff";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                staffIds.add(rs.getString("staff_id"));
            }
        }
        return staffIds;
    }

    public static List<String> getAllRoomIds() throws SQLException {
        List<String> roomIds = new ArrayList<>();
        String sql = "SELECT room_id FROM Room";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                roomIds.add(rs.getString("room_id"));
            }
        }
        return roomIds;
    }

    public static boolean maintenanceIdExists(String maintenanceId) throws SQLException {
        String sql = "SELECT 1 FROM Maintenance WHERE maintenance_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, maintenanceId);
            return pstm.executeQuery().next();
        }
    }

    public static boolean saveMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "INSERT INTO Maintenance (maintenance_id, room_id, staff_id, description, status) VALUES (?, ?, ?, ?, ?)";
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is unavailable");
        }
        connection.setAutoCommit(false);
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getMaintenanceId());
            pstm.setString(2, dto.getRoomId());
            pstm.setString(3, dto.getStaffId());
            pstm.setString(4, dto.getDescription());
            pstm.setString(5, dto.getStatus());

            boolean success = pstm.executeUpdate() > 0;
            if (success) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static boolean updateMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "UPDATE Maintenance SET room_id = ?, staff_id = ?, description = ?, status = ? WHERE maintenance_id = ?";
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is unavailable");
        }
        connection.setAutoCommit(false);
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getRoomId());
            pstm.setString(2, dto.getStaffId());
            pstm.setString(3, dto.getDescription());
            pstm.setString(4, dto.getStatus());
            pstm.setString(5, dto.getMaintenanceId());

            boolean success = pstm.executeUpdate() > 0;
            if (success) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static List<MaintenanceTM> getAllMaintenance() throws SQLException {
        List<MaintenanceTM> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM Maintenance";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                maintenanceList.add(new MaintenanceTM(
                        rs.getString("maintenance_id"),
                        rs.getString("description"),
                        rs.getString("staff_id"),
                        rs.getString("room_id"),
                        rs.getString("status")
                ));
            }
        }
        return maintenanceList;
    }

    public static boolean deleteMaintenance(String maintenanceId) throws SQLException {
        String sql = "DELETE FROM Maintenance WHERE maintenance_id = ?";
        Connection connection = DBConnection.getInstance().getConnection();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is unavailable");
        }
        connection.setAutoCommit(false);
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, maintenanceId);
            boolean success = pstm.executeUpdate() > 0;
            if (success) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}