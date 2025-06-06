package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.StaffDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffModel {
    public static List<StaffDTO> getAllStaff() throws SQLException {
        List<StaffDTO> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                staffList.add(new StaffDTO(
                        rs.getString("staff_id"),
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("position"),
                        rs.getString("contact"),
                        rs.getDate("hire_date")
                ));
            }
        }
        return staffList;
    }

    public static List<String> getAllUserIds() throws SQLException {
        List<String> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM user";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userIds.add(rs.getString("user_id"));
            }
        }
        return userIds;
    }

    public static String getNextStaffId() throws SQLException {
        String sql = "SELECT staff_id FROM staff ORDER BY staff_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(1));
                int nextId = lastIdNumberInt + 1;
                return String.format("S%03d", nextId);
            } else {
                return "S001";
            }
        }
    }


    public boolean deleteStaff(String staffId) throws SQLException {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStaff(StaffDTO staffDTO) throws SQLException {
        String sql = "UPDATE staff SET user_id = ?, name = ?, position = ?, contact = ?, hire_date = ? WHERE staff_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, staffDTO.getUserId());
            stmt.setString(2, staffDTO.getName());
            stmt.setString(3, staffDTO.getPosition());
            stmt.setString(4, staffDTO.getContact());
            stmt.setDate(5, staffDTO.getHireDate());
            stmt.setString(6, staffDTO.getStaffId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean saveStaff(StaffDTO staffDTO) throws SQLException {
            String sql = "INSERT INTO staff (staff_id, user_id, name, position, contact, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.getInstance().getConnection();
                 PreparedStatement stmt = con.prepareStatement(sql)) {

                stmt.setString(1, staffDTO.getStaffId());
                stmt.setString(2, staffDTO.getUserId());
                stmt.setString(3, staffDTO.getName());
                stmt.setString(4, staffDTO.getPosition());
                stmt.setString(5, staffDTO.getContact());
                stmt.setDate(6, staffDTO.getHireDate());

                return stmt.executeUpdate() >0;
            }
    }

    public StaffDTO searchStaffById(String staffId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StaffDTO(
                            rs.getString("staff_id"),
                            rs.getString("user_id"),
                            rs.getString("name"),
                            rs.getString("position"),
                            rs.getString("contact"),
                            rs.getDate("hire_date")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}
