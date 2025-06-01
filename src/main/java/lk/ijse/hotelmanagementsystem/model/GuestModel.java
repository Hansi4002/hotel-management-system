//package lk.ijse.hotelmanagementsystem.model;
//
//import lk.ijse.hotelmanagementsystem.dto.GuestDTO;
//import lk.ijse.hotelmanagementsystem.util.CrudUtil;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GuestModel {
//    public static String getNextGuestId() throws SQLException, ClassNotFoundException {
//        ResultSet resultSet = CrudUtil.execute("select guest_id from guest order by guest_id desc limit 1");
//        char tableCharacter = 'G';
//        if(resultSet.next()){
//            String lastId = resultSet.getString(1);
//            int lastIdNumber = Integer.parseInt(lastId.substring(1));
//            int nextId = lastIdNumber + 1;
//            String nextIdString = String.format("%03d", nextId);
//            return "G" + nextIdString;
//        }
//        return tableCharacter + "001";
//    }
//
//    public List<GuestDTO> getAllGuest() throws SQLException, ClassNotFoundException {
//        List<GuestDTO> guestDTOList = new ArrayList<>();
//        ResultSet resultSet = CrudUtil.execute("select * from guest");
//        while (resultSet.next()){
//            guestDTOList.add(new GuestDTO(
//                    resultSet.getString(1),
//                    resultSet.getString(2),
//                    resultSet.getDate(3),
//                    resultSet.getString(4),
//                    resultSet.getString(5),
//                    resultSet.getString(6),
//                    resultSet.getDate(7),
//                    resultSet.getString(8)
//            ));
//        }
//        return guestDTOList;
//    }
//
//    public boolean updateGuest(GuestDTO guestDTO) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("update guest SET name = ?, dob = ?, address = ?, contact = ?, email = ?, registrationdate = ?, loyaltystatus = ? where guest_id = ?)",
//                guestDTO.getName(),
//                guestDTO.getDob(),
//                guestDTO.getAddress(),
//                guestDTO.getContact(),
//                guestDTO.getEmail(),
//                guestDTO.getRegistrationDate(),
//                guestDTO.getLoyaltyStatus());
//    }
//
//    public boolean saveGuest(GuestDTO guestDTO) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("INSERT INTO Guest VALUES(?,?,?,?,?,?,?,?)",
//                guestDTO.getGuestId(),
//                guestDTO.getName(),
//                guestDTO.getDob(),
//                guestDTO.getAddress(),
//                guestDTO.getContact(),
//                guestDTO.getEmail(),
//                guestDTO.getRegistrationDate(),
//                guestDTO.getLoyaltyStatus());
//    }
//
//    public boolean deleteGuest(String guestId) throws SQLException, ClassNotFoundException {
//        return CrudUtil.execute("delete from guest where guest_id=?", guestId);
//    }
//}
