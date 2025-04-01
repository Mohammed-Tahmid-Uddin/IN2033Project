import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/in2033t32";
    private static final String user = "in2033t32_a";
    private static final String password = "bb53BZO9B5Y";

    public static void establishConnection(){
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection Established");
        } catch (SQLException e) {
            System.err.println("Connection Not Established");
        }
    }

    public static String getURL(){
        return url;
    }

    public static void main(String[] args){
        establishConnection();
    }

    public static ResultSet getEventsForDate(int year, int month, int day) {
        return null;
    }
}

