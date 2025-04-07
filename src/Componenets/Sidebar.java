package Componenets;

import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Sidebar {
    private JPanel sidebar;
    private boolean sidebarVisible = false;
    private Map<String, Map<String, Double>> roomPricing;

    public Sidebar() {
        
        initialiseSidebar();
        initializePricing();

    }

    private void initializePricing() {
        roomPricing = new HashMap<>();

        // Pricing for The Green Room
        Map<String, Double> greenRoomPricing = new HashMap<>();
        greenRoomPricing.put("1 Hour", 25.0);
        greenRoomPricing.put("Morning/Afternoon", 75.0);
        greenRoomPricing.put("All Day", 130.0);
        greenRoomPricing.put("Week", 600.0);
        roomPricing.put("The Green Room", greenRoomPricing);

        // Pricing for Brontë Boardroom
        Map<String, Double> bronteBoardroomPricing = new HashMap<>();
        bronteBoardroomPricing.put("1 Hour", 40.0);
        bronteBoardroomPricing.put("Morning/Afternoon", 120.0);
        bronteBoardroomPricing.put("All Day", 200.0);
        bronteBoardroomPricing.put("Week", 900.0);
        roomPricing.put("Brontë Boardroom", bronteBoardroomPricing);

        // Pricing for Dickens Den
        Map<String, Double> dickensDenPricing = new HashMap<>();
        dickensDenPricing.put("1 Hour", 30.0);
        dickensDenPricing.put("Morning/Afternoon", 90.0);
        dickensDenPricing.put("All Day", 150.0);
        dickensDenPricing.put("Week", 700.0);
        roomPricing.put("Dickens Den", dickensDenPricing);

        // Pricing for Poe Parlor
        Map<String, Double> poeParlorPricing = new HashMap<>();
        poeParlorPricing.put("1 Hour", 35.0);
        poeParlorPricing.put("Morning/Afternoon", 100.0);
        poeParlorPricing.put("All Day", 170.0);
        poeParlorPricing.put("Week", 800.0);
        roomPricing.put("Poe Parlor", poeParlorPricing);

        // Pricing for Globe Room
        Map<String, Double> globeRoomPricing = new HashMap<>();
        globeRoomPricing.put("1 Hour", 50.0);
        globeRoomPricing.put("Morning/Afternoon", 150.0);
        globeRoomPricing.put("All Day", 250.0);
        globeRoomPricing.put("Week", 1100.0);
        roomPricing.put("Globe Room", globeRoomPricing);

        // Pricing for Chekhov Chamber
        Map<String, Double> chekhovChamberPricing = new HashMap<>();
        chekhovChamberPricing.put("1 Hour", 38.0);
        chekhovChamberPricing.put("Morning/Afternoon", 110.0);
        chekhovChamberPricing.put("All Day", 180.0);
        chekhovChamberPricing.put("Week", 850.0);
        roomPricing.put("Chekhov Chamber", chekhovChamberPricing);

        // Pricing for Rehearsal Space (assumed, as not in the table)
        Map<String, Double> rehearsalSpacePricing = new HashMap<>();
        rehearsalSpacePricing.put("1 Hour", 20.0);
        rehearsalSpacePricing.put("Morning/Afternoon", 60.0);
        rehearsalSpacePricing.put("All Day", 100.0);
        rehearsalSpacePricing.put("Week", 500.0);
        roomPricing.put("Rehearsal Space", rehearsalSpacePricing);
    }

    private void initialiseSidebar() {
        sidebar = new JPanel();
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setPreferredSize(new Dimension(200, 600)); 
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Add buttons to the sidebar
        addSidebarButton("Home");
        addSidebarButton("Create Report");
        addSidebarButton("Book Other Rooms");
       

        // Initially hide the sidebar
        sidebar.setVisible(false);
    }

    private void addSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40)); 
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        if (text.equals("Book Other Rooms")){
            button.addActionListener(e -> showRoomBookingDialog());
        }
    }

    public JPanel getSidebar() {
        return sidebar;
    }

    public void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
    }

    // Method to show the room booking dialog
    private void showRoomBookingDialog() {
        String[] roomOptions = {"Rehearsal Space", "The Green Room", "Brontë Boardroom", "Dickens Den", "Poe Parlor", "Globe Room", "Chekhov Chamber"};
        String selectedRoom = (String) JOptionPane.showInputDialog(
                null,
                "Select a room to book:",
                "Book Other Rooms",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roomOptions,
                roomOptions[0]
        );

        if (selectedRoom != null) {
            // Prompt for setup type
            String[] setupOptions = {"Classroom", "Boardroom", "Presentation"};
            String selectedSetup = (String) JOptionPane.showInputDialog(
                    null,
                    "Select the room setup for " + selectedRoom + ":",
                    "Room Setup",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    setupOptions,
                    setupOptions[0]
            );

            if (selectedSetup != null) {
                // Prompt for duration
                String[] durationOptions = {"1 Hour", "Morning/Afternoon", "All Day", "Week"};
                String selectedDuration = (String) JOptionPane.showInputDialog(
                        null,
                        "Select the booking duration for " + selectedRoom + ":",
                        "Booking Duration",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        durationOptions,
                        durationOptions[0]
                );

                if (selectedDuration != null) {
                    // Calculate the price based on room and duration
                    double price = roomPricing.get(selectedRoom).get(selectedDuration);
                    bookRoom(selectedRoom, selectedSetup, selectedDuration, price);
                }
            }
        }
    }

    private void bookRoom(String roomName, String setupType, String duration, double price) {
        String bookingDate = "2025-04-05"; // Hardcoded for now; replace with dynamic date if needed
        if (!isRoomBooked(roomName, bookingDate)) {
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(
                         "INSERT INTO BookingRoom (room_id, booking_date, total_price, setup_type, duration) " +
                                 "VALUES ((SELECT room_id FROM Rooms WHERE room_name = ? LIMIT 1), ?, ?, ?, ?)")) {
                pstmt.setString(1, roomName);
                pstmt.setString(2, bookingDate);
                pstmt.setDouble(3, price);
                pstmt.setString(4, setupType);
                pstmt.setString(5, duration);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, roomName + " booked successfully for " + bookingDate + " with " + setupType + " setup for " + duration + ". Total price: £" + price);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to book " + roomName + ".");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error booking " + roomName + ": " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, roomName + " is already booked on " + bookingDate + ".");
        }
    }

    private boolean isRoomBooked(String roomName, String date) {
        String checkBooking = "SELECT COUNT(*) FROM BookingRoom WHERE room_id = (SELECT room_id FROM Rooms WHERE room_name = ?) AND booking_date = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(checkBooking)) {
            pstmt.setString(1, roomName);
            pstmt.setString(2, date);
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
}
