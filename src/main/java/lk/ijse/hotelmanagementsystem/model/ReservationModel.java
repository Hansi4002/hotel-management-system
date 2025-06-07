package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.ReservationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    public static String getNextReservationId() throws SQLException {
        String sql = "SELECT reservation_id FROM Reservation WHERE reservation_id REGEXP '^RES[0-9]{3}$' ORDER BY CAST(SUBSTRING(reservation_id, 4) AS UNSIGNED) DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int num = Integer.parseInt(lastId.substring(3)) + 1;
                String newId = String.format("RES%03d", num);
                if (isReservationIdExists(newId)) {
                    throw new SQLException("Generated reservation_id exists: " + newId);
                }
                return newId;
            }
            return "RES001";
        }
    }

    private static boolean isReservationIdExists(String reservationId) throws SQLException {
        String sql = "SELECT 1 FROM Reservation WHERE reservation_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            return ps.executeQuery().next();
        }
    }

    public static List<ReservationDTO> getAllReservations() throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM Reservation";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservationList.add(new ReservationDTO(
                        rs.getString("reservation_id"),
                        rs.getString("guest_id"),
                        rs.getString("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getTimestamp("booking_time"),
                        rs.getInt("num_guests"),
                        rs.getString("status"),
                        rs.getDouble("total_cost")
                ));
            }
        }
        return reservationList;
    }

    public static boolean saveReservation(ReservationDTO dto) throws SQLException {
        if (dto.getReservationId() == null || dto.getReservationId().isEmpty()) {
            throw new SQLException("Reservation ID cannot be null or empty");
        }
        String sql = "INSERT INTO Reservation (reservation_id, guest_id, room_id, check_in_date, check_out_date, booking_time, num_guests, status, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getReservationId());
            stm.setString(2, dto.getGuestId());
            stm.setString(3, dto.getRoomId());
            stm.setDate(4, dto.getCheckInDate());
            stm.setDate(5, dto.getCheckOutDate());
            stm.setTimestamp(6, dto.getBookingTime());
            stm.setInt(7, dto.getNumberOfGuests());
            stm.setString(8, dto.getStatus());
            stm.setDouble(9, dto.getTotalCost());
            boolean success = stm.executeUpdate() > 0;
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

    public static boolean deleteReservation(String reservationId) throws SQLException {
        String sql = "DELETE FROM Reservation WHERE reservation_id = ?";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, reservationId);
            boolean success = stm.executeUpdate() > 0;
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

    public static boolean updateReservation(ReservationDTO dto) throws SQLException {
        String sql = "UPDATE Reservation SET guest_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, booking_time = ?, num_guests = ?, status = ?, total_cost = ? WHERE reservation_id = ?";
        Connection con = DBConnection.getInstance().getConnection();
        con.setAutoCommit(false);
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getGuestId());
            stm.setString(2, dto.getRoomId());
            stm.setDate(3, dto.getCheckInDate());
            stm.setDate(4, dto.getCheckOutDate());
            stm.setTimestamp(5, dto.getBookingTime());
            stm.setInt(6, dto.getNumberOfGuests());
            stm.setString(7, dto.getStatus());
            stm.setDouble(8, dto.getTotalCost());
            stm.setString(9, dto.getReservationId());
            boolean success = stm.executeUpdate() > 0;
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

    public static List<String> loadGuestIds() throws SQLException {
        List<String> guestIds = new ArrayList<>();
        String sql = "SELECT guest_id FROM Guest";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                guestIds.add(rs.getString("guest_id"));
            }
        }
        return guestIds;
    }

    public static List<String> loadRoomIds() throws SQLException {
        List<String> roomIds = new ArrayList<>();
        String sql = "SELECT room_id FROM Room WHERE status = 'Available'";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomIds.add(rs.getString("room_id"));
            }
        }
        return roomIds;
    }

    public static ReservationDTO searchReservationById(String reservationId) throws SQLException {
        String sql = "SELECT * FROM Reservation WHERE reservation_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ReservationDTO(
                            rs.getString("reservation_id"),
                            rs.getString("guest_id"),
                            rs.getString("room_id"),
                            rs.getDate("check_in_date"),
                            rs.getDate("check_out_date"),
                            rs.getTimestamp("booking_time"),
                            rs.getInt("num_guests"),
                            rs.getString("status"),
                            rs.getDouble("total_cost")
                    );
                }
            }
        }
        return null;
    }

    public double getRoomPriceById(String roomId) throws SQLException {
        String sql = "SELECT price FROM Room WHERE room_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
                throw new SQLException("Room not found: " + roomId);
            }
        }
    }
}