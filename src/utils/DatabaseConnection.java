package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://sst-stuproj.city.ac.uk/in2033t31";  // Your database URL
    private static final String USER = "in2033t31_a";  // Your database username
    private static final String PASSWORD = "pw5_K5DQSBk";  // Your database password

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to establish connection.");
            e.printStackTrace();
        }
        return connection;
    }

    public static String getURL() {
        return DB_URL;
    }

    public static void main(String[] args) {
        getConnection();  // Run connection test
    }
}
