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
    public static List<String> getAllStaffIds() throws SQLException, SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = "SELECT staffId FROM Staff";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                staffIds.add(rs.getString("staffId"));
            }
        }
        return staffIds;
    }

    public static List<String> getAllRoomIds() throws SQLException {
        List<String> roomIds = new ArrayList<>();
        String sql = "SELECT roomId FROM Room";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                roomIds.add(rs.getString("roomId"));
            }
        }
        return roomIds;
    }

    public static boolean updateMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "UPDATE Maintenance SET description = ?, staffId = ?, roomId = ?, status = ? WHERE maintenanceId = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getDescription());
            pstm.setString(2, dto.getStaffId());
            pstm.setString(3, dto.getRoomId());
            pstm.setString(4, dto.getStatus());
            pstm.setString(5, dto.getMaintenanceId());

            return pstm.executeUpdate() > 0;
        }
    }

    public static boolean saveMaintenance(MaintenanceDTO dto) throws SQLException {
        String sql = "INSERT INTO Maintenance (maintenanceId, description, staffId, roomId, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, dto.getMaintenanceId());
            pstm.setString(2, dto.getDescription());
            pstm.setString(3, dto.getStaffId());
            pstm.setString(4, dto.getRoomId());
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
                        rs.getString("description"),
                        rs.getString("staffId"),
                        rs.getString("roomId"),
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
