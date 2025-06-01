//package lk.ijse.hotelmanagementsystem.model;
//
//import lk.ijse.hotelmanagementsystem.dto.ReservationDTO;
//import lk.ijse.hotelmanagementsystem.util.CrudUtil;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReservationModel {
//    public static String getNextReservationId() throws SQLException, ClassNotFoundException {
//        ResultSet rs = CrudUtil.execute("SELECT reservation_id FROM reservation ORDER BY reservation_id DESC LIMIT 1");
//        char tableChar = 'R';
//
//        if (rs.next()) {
//            String lastId = rs.getString(1);
//            int lastIdNumberInt = Integer.parseInt(lastId.substring(1));
//            int nextId = lastIdNumberInt + 1;
//            String nextIdString = String.format("%03d", nextId);
//            return "RE" + nextIdString;
//        }
//        return tableChar + "001";
//    }
//
//    public List<ReservationDTO> getAllReservations() throws SQLException, ClassNotFoundException {
//        List<ReservationDTO> reservationList = new ArrayList<>();
//        ResultSet resultSet = CrudUtil.execute("SELECT * FROM reservation");
//
//        while (resultSet.next()) {
//            reservationList.add(new ReservationDTO(
//                    resultSet.getString("reservation_id"),
//                    resultSet.getString("guest_id"),
//                    resultSet.getString("room_id"),
//                    resultSet.getDate("check_in_date"),
//                    resultSet.getDate("check_out_date"),
//                    resultSet.getTime("booking_time"),
//                    resultSet.getString("number_of_guests"),
//                    resultSet.getInt("status"),
//                    resultSet.getDouble("total_cost")
//            ));
//        }
//        return reservationList;
//    }
//    public boolean saveReservation(ReservationDTO dto) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("INSERT INTO reservation VALUES(?,?,?,?,?,?,?,?,?)",
//                dto.getReservationId(),
//                dto.getGuestId(),
//                dto.getRoomId(),
//                dto.getCheckInDate(),
//                dto.getCheckOutDate(),
//                dto.getBookingTime(),
//                dto.getNumberOfGuests(),
//                dto.getStatus(),
//                dto.getTotalCost());
//    }
//
//    public boolean updateReservation(ReservationDTO dto) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("UPDATE reservation SET guest_id=?, room_id=?, check_in_date=?, check_out_date=?, booking_time=?, number_of_guests=?, status=?, total_cost=? WHERE reservation_id=?",
//                dto.getGuestId(),
//                dto.getRoomId(),
//                dto.getCheckInDate(),
//                dto.getCheckOutDate(),
//                dto.getBookingTime(),
//                dto.getNumberOfGuests(),
//                dto.getStatus(),
//                dto.getTotalCost(),
//                dto.getReservationId());
//    }
//
//    public boolean deleteReservation(String reservationId) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("DELETE FROM reservation WHERE reservation_id=?", reservationId);
//    }
//}
//}
