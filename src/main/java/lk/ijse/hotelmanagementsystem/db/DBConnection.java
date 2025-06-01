package lk.ijse.hotelmanagementsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection dbConnection;
    private Connection connection;

    // Database credentials (modify according to your setup)
    private static final String URL = "jdbc:mysql://localhost:3306/hotelmanagementsystem"; // ✅ Replace with your DB name
    private static final String USERNAME = "root"; // ✅ Replace with your DB username
    private static final String PASSWORD = "mySQL"; // ✅ Replace with your DB password

    private DBConnection() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null || dbConnection.getConnection().isClosed()) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}