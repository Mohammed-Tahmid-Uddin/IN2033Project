import javax.swing.JFrame;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class App {
    public static void main(String[] args) throws Exception {
        JFrame Window=new JFrame();
        DatabaseConnection.getConnection();


        Window.setVisible(true);
        Window.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        Window.setTitle("Lancaster's Music Hall App");
        Window.getContentPane().setBackground(Color.WHITE);

        Window.setResizable(false);

       // SeatBooking seatBooking=new SeatBooking(Window);
        SmallerHall hall=new SmallerHall(Window);

    }


    private static void insertInitialData() {
    // Checkd if a show with the same date and time already exists
    if (!doesShowExist("2023-12-15", "19:00:00")) {
        // Inserts into Rooms table
        String insertRoom = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Main Hall', 300, 'Main performance hall')";
        executeUpdate(insertRoom);

        // Inserts into Shows table
        String insertShow = "INSERT INTO Shows (title, date, time, venue) VALUES ('Sample Show', '2023-12-15', '19:00:00', 'Main Hall')";
        executeUpdate(insertShow);

        // Inserts into BookingRoom table
        String insertBookingRoom = "INSERT INTO BookingRoom (show_id, room_id, booking_date, total_price) " +
                                       "VALUES ((SELECT show_id FROM Shows WHERE title = 'Sample Show'), " +
                                               "(SELECT room_id FROM Rooms WHERE room_name = 'Main Hall' LIMIT 1), " +
                                               "'2023-11-01', 0.00)";
        executeUpdate(insertBookingRoom);
    } else {
        System.out.println("A show with the same date and time already exists.");
    }
}

private static boolean doesShowExist(String date, String time) {
    String checkShow = "SELECT COUNT(*) FROM Shows WHERE date = ? AND time = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(checkShow)) {
        pstmt.setString(1, date);
        pstmt.setString(2, time);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
    }

    private static void executeUpdate(String sql) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error executing update.");
            e.printStackTrace();
        }
    }
}