package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.RoomDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomModel {

    public static String getNextRoomId() throws SQLException, ClassNotFoundException {
        String sql = "SELECT room_id FROM room ORDER BY room_id DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                int lastIdNumberInt = Integer.parseInt(lastId.substring(1));
                int nextId = lastIdNumberInt + 1;
                return String.format("R%03d", nextId);
            } else {
                return "R001";
            }
        }
    }

    public List<RoomDTO> getAllRooms() throws SQLException, ClassNotFoundException {
        List<RoomDTO> roomList = new ArrayList<>();
        String sql = "SELECT * FROM room";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getString("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("floor_number"),
                        rs.getInt("capacity"),
                        rs.getString("description"),
                        rs.getString("room_number")
                );
                roomList.add(room);
            }
        }
        return roomList;
    }

    public boolean saveRoom(RoomDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO room (room_id, room_type, price, status, floor_number, capacity, description, room_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, dto.getRoomId());
            stm.setString(2, dto.getRoomType());
            stm.setDouble(3, dto.getPrice());
            stm.setString(4, dto.getStatus());
            stm.setString(5, dto.getFloorNumber());
            stm.setInt(6, dto.getCapacity());
            stm.setString(7, dto.getDescription());
            stm.setString(8, dto.getRoomNumber());

            return stm.executeUpdate() > 0;
        }
    }

    public boolean updateRoom(RoomDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE room SET room_type=?, price=?, status=?, floor_number=?, capacity=?, description=?, room_number=? WHERE room_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, dto.getRoomType());
            stm.setDouble(2, dto.getPrice());
            stm.setString(3, dto.getStatus());
            stm.setString(4, dto.getFloorNumber());
            stm.setInt(5, dto.getCapacity());
            stm.setString(6, dto.getDescription());
            stm.setString(7, dto.getRoomNumber());
            stm.setString(8, dto.getRoomId());

            return stm.executeUpdate() > 0;
        }
    }

    public boolean deleteRoom(String roomId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM room WHERE room_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, roomId);
            return stm.executeUpdate() > 0;
        }
    }
}