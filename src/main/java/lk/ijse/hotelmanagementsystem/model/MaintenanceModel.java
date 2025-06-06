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

    public static boolean updateMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "UPDATE Maintenance SET room_id = ?, staff_id = ?, description = ?, status = ? WHERE maintenanceId = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getRoomId());
            pstm.setString(2, dto.getStaffId());
            pstm.setString(3, dto.getDescription());
            pstm.setString(4, dto.getStatus());
            pstm.setString(5, dto.getMaintenanceId());

            return pstm.executeUpdate() > 0;
        }
    }

    public static boolean saveMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "INSERT INTO Maintenance (maintenance_id, room_id, staff_id, description, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getMaintenanceId());
            pstm.setString(2, dto.getRoomId());
            pstm.setString(3, dto.getStaffId());
            pstm.setString(4, dto.getDescription());
            pstm.setString(5, dto.getStatus());

            return pstm.executeUpdate() > 0;
        }
    }

    public List<MaintenanceTM> getAllMaintenance() throws SQLException {
        List<MaintenanceTM> maintenanceList = new ArrayList<>();
        String sql = "SELECT * FROM Maintenance";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                maintenanceList.add(new MaintenanceTM(
                        rs.getString("maintenanceId"),
                        rs.getString("roomId"),
                        rs.getString("staffId"),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
        }
        return maintenanceList;
    }

    public boolean deleteMaintenance(String maintenanceId) throws SQLException {
        String sql = "DELETE FROM Maintenance WHERE maintenanceId = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, maintenanceId);
            return pstm.executeUpdate() > 0;
        }
    }
}
