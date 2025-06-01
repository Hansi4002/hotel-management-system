//package lk.ijse.hotelmanagementsystem.util;
//
//import lk.ijse.hotelmanagementsystem.db.DBConnection;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class CrudUtil {
//    public static <T> T execute(String sql, Object... obj) throws SQLException, ClassNotFoundException {
//        Connection connection = DBConnection.getInstance().getConnection();
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        for (int i = 0; i < obj.length; i++) {
//            preparedStatement.setObject(i + 1, obj[i]);
//        }
//        if(sql.startsWith("SELECT") || sql.startsWith("select")){
//            ResultSet resultSet = preparedStatement.executeQuery();
//            return (T) resultSet;
//        }else {
//            int updateCount = preparedStatement.executeUpdate();
//            boolean isSuccess = updateCount > 0;
//            return (T)(Boolean)isSuccess;
//        }
//    }
//}
