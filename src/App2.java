import javax.swing.JFrame;

import models.SeatBooking;
import models.SmallerHall;
import utils.DatabaseConnection;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class App2 {
    public static void main(String[] args) throws Exception {
        JFrame Window=new JFrame();
        DatabaseConnection.getConnection();
        insertData();
        Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Window.setVisible(true);
        Window.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        Window.setTitle("Lancaster's Music Hall App");
        Window.getContentPane().setBackground(Color.WHITE);
        if (!doesShowExist("2023-12-15", "19:00:00")) {
            insertData();
        }

        Window.setResizable(false);

        SeatBooking seatBooking=new SeatBooking(Window);

    }


    private static void insertData() {
    // Checkd if a show with the same date and time already exists
    if (!doesShowExist("2023-12-15", "19:00:00")) {
        String insertGreenRoom = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('The Green Room', 12, 'Small meeting room, Classroom: 12, Boardroom: 10, Presentation: 20')";
        executeUpdate(insertGreenRoom);

        String insertBronteBoardroom = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Brontë Boardroom', 25, 'Medium meeting room, Classroom: 25, Boardroom: 18, Presentation: 40')";
        executeUpdate(insertBronteBoardroom);

        String insertDickensDen = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Dickens Den', 15, 'Compact meeting room, Classroom: 15, Boardroom: 12, Presentation: 25')";
        executeUpdate(insertDickensDen);

        String insertPoeParlor = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Poe Parlor', 20, 'Versatile meeting room, Classroom: 20, Boardroom: 14, Presentation: 30')";
        executeUpdate(insertPoeParlor);

        String insertGlobeRoom = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Globe Room', 30, 'Large meeting room, Classroom: 30, Boardroom: 20, Presentation: 50')";
        executeUpdate(insertGlobeRoom);

        String insertChekhovChamber = "INSERT INTO Rooms (room_name, capacity, description) VALUES ('Chekhov Chamber', 18, 'Medium meeting room, Classroom: 18, Boardroom: 16, Presentation: 35')";
        executeUpdate(insertChekhovChamber);


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
