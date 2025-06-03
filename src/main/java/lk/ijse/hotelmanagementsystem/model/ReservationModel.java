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
        String sql = "SELECT reservation_id FROM reservation WHERE reservation_id REGEXP '^RES[0-9]{3}$' ORDER BY CAST(SUBSTRING(reservation_id, 4) AS UNSIGNED) DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(3));
                int nextId = lastIdNumberInt + 1;
                return String.format("RES%03d", nextId);
            } else {
                return "RES001";
            }
        }
    }

    public static List<ReservationDTO> getAllReservations() throws SQLException {
        List<ReservationDTO> reservationList = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ReservationDTO reservation = new ReservationDTO(
                        rs.getString("reservation_id"),
                        rs.getString("guest_id"),
                        rs.getString("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getTimestamp("booking_time") != null ? rs.getTimestamp("booking_time") : null,
                        rs.getInt("num_guests"),
                        rs.getString("status"),
                        rs.getDouble("total_cost")
                );
                reservationList.add(reservation);
            }
        }
        return reservationList;
    }

    public static boolean saveReservation(ReservationDTO dto) throws SQLException {
        String sql = "INSERT INTO reservation (reservation_id, guest_id, room_id, check_in_date, check_out_date, booking_time, num_guests, status, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getReservationId());
            stm.setString(2, dto.getGuestId());
            stm.setString(3, dto.getRoomId());
            stm.setDate(4, dto.getCheckInDate());
            stm.setDate(5, dto.getCheckOutDate());
            stm.setTimestamp(6, dto.getBookingTime());
            stm.setInt(7, dto.getNumberOfGuests());
            stm.setString(8, dto.getStatus());
            stm.setDouble(9, dto.getTotalCost());
            return stm.executeUpdate() > 0;
        }
    }

    public static boolean deleteReservation(String reservationId) throws SQLException {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, reservationId);
            return stm.executeUpdate() > 0;
        }
    }

    public static boolean updateReservation(ReservationDTO dto) throws SQLException {
        String sql = "UPDATE reservation SET guest_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, booking_time = ?, num_guests = ?, status = ?, total_cost = ? WHERE reservation_id = ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, dto.getGuestId());
            stm.setString(2, dto.getRoomId());
            stm.setDate(3, dto.getCheckInDate());
            stm.setDate(4, dto.getCheckOutDate());
            stm.setTimestamp(5, dto.getBookingTime());
            stm.setInt(6, dto.getNumberOfGuests());
            stm.setString(7, dto.getStatus());
            stm.setDouble(8, dto.getTotalCost());
            stm.setString(9, dto.getReservationId());
            return stm.executeUpdate() > 0;
        }
    }

    public static List<String> loadGuestIds() throws SQLException {
        List<String> guestIds = new ArrayList<>();
        String sql = "SELECT guest_id FROM guest";
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
        String sql = "SELECT room_id FROM room";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomIds.add(rs.getString("room_id"));
            }
        }
        return roomIds;
    }

    public static List<String> getAllReservationIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT reservation_id FROM reservation";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("reservation_id"));
            }
        }
        return ids;
    }
}