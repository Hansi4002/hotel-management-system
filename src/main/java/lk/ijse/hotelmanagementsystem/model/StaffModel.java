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
    public List<StaffDTO> getAllStaff() throws SQLException {
        List<StaffDTO> staffList = new ArrayList<>();
        String sql = "SELECT * FROM Staff";
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

    public List<String> getAllUserIds() throws SQLException {
        List<String> userIds = new ArrayList<>();
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT user_id FROM User";
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            userIds.add(rs.getString("user_id"));
        }

        return userIds;
    }

    public String getNextStaffId() throws SQLException {
        String sql = "SELECT staff_id FROM Staff ORDER BY staff_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("staff_id");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                String newId = String.format("S%03d", num);
                if (isStaffIdExists(newId)) {
                    throw new SQLException("Generated staff_id exists: " + newId);
                }
                return newId;
            }
            return "S001";
        }
    }

    private boolean isStaffIdExists(String staffId) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE staff_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, staffId);
            return ps.executeQuery().next();
        }
    }

    public boolean deleteStaff(String staffId) throws SQLException {
        String sql = "DELETE FROM Staff WHERE staff_id = ?";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                con.commit();
            } else {
                con.rollback();
            }
            return success;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public boolean updateStaff(StaffDTO staffDTO) throws SQLException {
        String sql = "UPDATE Staff SET user_id = ?, name = ?, position = ?, contact = ?, hire_date = ? WHERE staff_id = ?";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, staffDTO.getUserId());
            stmt.setString(2, staffDTO.getName());
            stmt.setString(3, staffDTO.getPosition());
            stmt.setString(4, staffDTO.getContact());
            stmt.setDate(5, staffDTO.getHireDate());
            stmt.setString(6, staffDTO.getStaffId());
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                con.commit();
            } else {
                con.rollback();
            }
            return success;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public boolean saveStaff(StaffDTO staffDTO) throws SQLException {
        String sql = "INSERT INTO Staff (staff_id, user_id, name, position, contact, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, staffDTO.getStaffId());
            stmt.setString(2, staffDTO.getUserId());
            stmt.setString(3, staffDTO.getName());
            stmt.setString(4, staffDTO.getPosition());
            stmt.setString(5, staffDTO.getContact());
            stmt.setDate(6, staffDTO.getHireDate());
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                con.commit();
            } else {
                con.rollback();
            }
            return success;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public StaffDTO searchStaffById(String staffId) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE staff_id = ?";
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
                }
                return null;
            }
        }
    }
}