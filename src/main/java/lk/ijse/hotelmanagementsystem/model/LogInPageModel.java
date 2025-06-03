package lk.ijse.hotelmanagementsystem.model;

import lk.ijse.hotelmanagementsystem.db.DBConnection;
import lk.ijse.hotelmanagementsystem.dto.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInPageModel {

    public static UserDTO login(String email, String password) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();

        String sql = "SELECT * FROM User WHERE email = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String userId = resultSet.getString("user_id");
            String role = resultSet.getString("role");

            return new UserDTO(userId, email, password, role);
        }

        return null; 
    }
}
