package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.GuestDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestModel {

    public static String getNextGuestId() throws SQLException, ClassNotFoundException {
        String sql = "SELECT guest_id FROM Guest ORDER BY guest_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(1));
                int nextId = lastIdNumberInt + 1;
                return String.format("G%03d", nextId);
            } else {
                return "G001";
            }
        }
    }

    public List<GuestDTO> getAllGuests() throws SQLException, ClassNotFoundException {
        List<GuestDTO> guestList = new ArrayList<>();
        String sql = "SELECT * FROM Guest";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                GuestDTO guest = new GuestDTO(
                        rs.getString("guest_id"),
                        rs.getString("name"),
                        rs.getDate("dob"),
                        rs.getString("address"),
                        rs.getString("contact"),
                        rs.getString("email"),
                        rs.getDate("registration_date"),
                        rs.getString("loyalty_status")
                );
                guestList.add(guest);
            }
        }
        return guestList;
    }

    public boolean saveGuest(GuestDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Guest (guest_id, name, dob, address, contact, email, registration_date, loyalty_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, dto.getGuestId());
            stm.setString(2, dto.getName());
            stm.setDate(3, new java.sql.Date(dto.getDob().getTime()));
            stm.setString(4, dto.getAddress());
            stm.setString(5, dto.getContact());
            stm.setString(6, dto.getEmail());
            stm.setDate(7, new java.sql.Date(dto.getRegistrationDate().getTime()));
            stm.setString(8, dto.getLoyaltyStatus());

            return stm.executeUpdate() > 0;
        }
    }

    public boolean updateGuest(GuestDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Guest SET name=?, dob=?, address=?, contact=?, email=?, registration_date=?, loyalty_status=? WHERE guest_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, dto.getName());
            stm.setDate(2, new java.sql.Date(dto.getDob().getTime()));
            stm.setString(3, dto.getAddress());
            stm.setString(4, dto.getContact());
            stm.setString(5, dto.getEmail());
            stm.setDate(6, new java.sql.Date(dto.getRegistrationDate().getTime()));
            stm.setString(7, dto.getLoyaltyStatus());
            stm.setString(8, dto.getGuestId());

            return stm.executeUpdate() > 0;
        }
    }

    public boolean deleteGuest(String guestId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Guest WHERE guest_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, guestId);
            return stm.executeUpdate() > 0;
        }
    }
}