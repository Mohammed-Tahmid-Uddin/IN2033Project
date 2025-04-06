package models;
import javax.swing.*;

import Componenets.Sidebar;
import utils.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class SeatBooking {
    private Map<String, SeatStatus> seats; 
    private JButton[][] stallButtons;
    private JButton[][] balconyButtons;
    private int roomId;
  
    private enum SeatStatus {AVAILABLE, BOOKED, RESERVED, WHEELCHAIR}

    private Sidebar sidebar; 
    private JFrame window;


    private static final int BUTTON_WIDTH = 30; 
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTONS_PER_ROW = 20;
    private int showId;
    private int BookingId;


    public SeatBooking(JFrame window) {
        seats = new HashMap<>();
        this.showId = getLatestShowId();
        this.roomId = getLatestRoomID(); 
        this.BookingId=getLatestBookingId();
        initialiseGui(window);
        loadSeat(); 
        updateButton(); 
    }

    

    private void loadSeat() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT seat_number, status FROM SeatStates WHERE show_id = ? AND room_id = ?")) {
            pstmt.setInt(1, showId);
            pstmt.setInt(2, roomId); 
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String seatId = rs.getString("seat_number");
                String status = rs.getString("status");
                seats.put(seatId, SeatStatus.valueOf(status));
            }
            System.out.println("Loading seats for show ID: " + showId + " and room ID: " + roomId);
        } catch (SQLException e) {
            System.err.println("Error loading seat bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateButton() {
        seats.forEach((seatId, status) -> {
            JButton button = findSeatButton(seatId);
            if (button != null) {
                button.setEnabled(false);
                button.setText("");
                button.setBackground(
                    status == SeatStatus.WHEELCHAIR ? Color.BLUE :
                    status == SeatStatus.RESERVED ? Color.GREEN :
                    status == SeatStatus.BOOKED ? Color.ORANGE :
                    Color.WHITE
                );
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        });
    }

    private void initialiseGui(JFrame window) {
        // Creates a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Creates the menu button
        sidebar=new Sidebar();
        mainPanel.add(sidebar.getSidebar(), BorderLayout.WEST);
        JButton menuButton = createMenuButton();

        // Combining the menu button and screen button into a single panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Menu button aligned to the left
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.add(menuButton);
        topPanel.add(menuPanel, BorderLayout.WEST);


        // Create the stalls panel
        JPanel stallsPanel = createSeatPanel(285, "S ", new Color(240, 240, 240));
        JPanel balconyPanel = createSeatPanel(89, "B ", new Color(240, 240, 240)); 

        // Combine stalls and balcony panels into a single panel
        JPanel seatPanels = new JPanel(new GridLayout(2, 1, 10, 10));
        seatPanels.add(stallsPanel);
        seatPanels.add(balconyPanel);

        // Add the seat panels to the main panel
         mainPanel.add(seatPanels, BorderLayout.CENTER);

        // Add a refund button
        JButton refundButton = new JButton("Refund Seat");
        refundButton.addActionListener(e -> showRefundOption());
        menuPanel.add(refundButton);


        // Creates the screen panel
        JPanel screenPanel = createScreenPanel();
        // Screen button centered
        topPanel.add(screenPanel, BorderLayout.CENTER);

        



        // Adds the main panel to the window
        mainPanel.add(topPanel, BorderLayout.NORTH);
        window.add(mainPanel);
        window.revalidate(); 
    }

    private JButton createMenuButton() {
        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 24));
        menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> sidebar.toggleSidebar());
        return menuButton;
    }


    private JPanel createScreenPanel() {
        JPanel screenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JButton screenButton = new JButton("Screen");
        screenButton.setEnabled(false);
        screenButton.setFont(new Font("Arial", Font.BOLD, 24));
        screenButton.setBackground(Color.WHITE);
        screenButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        screenButton.setFocusPainted(false);
        screenButton.setOpaque(true); 

        Dimension screenButtonSize = new Dimension(300, 50);
        screenButton.setPreferredSize(screenButtonSize);
        screenPanel.add(screenButton);

        return screenPanel;
    }

    private JPanel createSeatPanel(int numberOfSeats, String seatPrefix, Color backgroundColor) {
        JPanel seatPanel = new JPanel(new GridLayout(0, BUTTONS_PER_ROW, 5, 5));
        seatPanel.setBackground(backgroundColor);

        // Initialises the 2D array for seat buttons
        int rows = (numberOfSeats / BUTTONS_PER_ROW) + 1;
        JButton[][] buttons = new JButton[rows][BUTTONS_PER_ROW];

    
        for (int i = 1; i <= numberOfSeats; i++) {
            String seatId = seatPrefix + i;
            JButton seatButton = new JButton(seatId);
            seatButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            seatButton.setEnabled(true);
            seatButton.setBackground(Color.WHITE);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            seatPanel.add(seatButton);
            seatButton.addActionListener(e -> showSeatOptions(seatId, seatButton));

             // Calculate row and column for the seat
             int row = (i - 1) / BUTTONS_PER_ROW;
             int col = (i - 1) % BUTTONS_PER_ROW;
             buttons[row][col] = seatButton; // Store the button in the 2D array
 
             seatPanel.add(seatButton);
        }



        // Store the 2D array for later use
        if (seatPrefix.equals("S ")) {
            stallButtons = buttons;
        } else {
            balconyButtons = buttons;
        }


    
        return seatPanel;
    }

    private void showSeatOptions(String seatId, JButton seatButton) {
        String[] options = {"Book", "Reserve", "Wheelchair User"};
        int choice = JOptionPane.showOptionDialog(
                window,
                "Pick an option for " + seatId,
                "Seat Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
    
        switch (choice) {
            case 0:
                updateSeatStatus(seatId, seatButton, SeatStatus.BOOKED);
                break;
            case 1:
                updateSeatStatus(seatId, seatButton, SeatStatus.RESERVED);
                break;
            case 2:
                updateSeatStatus(seatId, seatButton, SeatStatus.WHEELCHAIR);
                    int row = (Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1) / BUTTONS_PER_ROW;
            int col = (Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1) % BUTTONS_PER_ROW;
            boolean adjacentSeatReserved = offerAdjacentSeatChoice(row, col, seatId.startsWith("S ") ? stallButtons : balconyButtons);
            if (!adjacentSeatReserved) {
                revertSeatStatus(seatId, seatButton);
        }
                break;
            default:
                break;
        }
    }

    private void updateSeatStatus(String seatId, JButton seatButton, SeatStatus status) {
        if (seats.getOrDefault(seatId, SeatStatus.AVAILABLE) == SeatStatus.AVAILABLE) {
            seats.put(seatId, status);
            seatButton.setEnabled(false);
            seatButton.setText(""); // Removes the text
            seatButton.setBackground(
                status == SeatStatus.WHEELCHAIR ? Color.BLUE :
                status == SeatStatus.RESERVED ? Color.GREEN :
                Color.ORANGE);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JOptionPane.showMessageDialog(window, seatId + " is now " + status + ".");
            saveSeatBooking(seatId, status); 
        }else {
                JOptionPane.showMessageDialog(window, seatId + " is already taken.");
            }
        }
    

    private boolean offerAdjacentSeatChoice(int row, int col, JButton[][] buttons) {
        String[] options = {"Left Seat", "Right Seat", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                window,
                "Remove which adjacent seat?",
                "Wheelchair Accessibility",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        boolean isLeft = (choice == 0);
        if (choice == 1) isLeft = false;
        else if (choice == 2) return false; // Cancel
    
        if (isValidAdjacentSeat(row, col, isLeft, buttons)) {
            int targetCol = isLeft ? col - 1 : col + 1;
            JButton adjacentButton = buttons[row][targetCol];
            adjacentButton.setText(""); // Remove text
            adjacentButton.setEnabled(false);
            adjacentButton.setBackground(Color.GRAY);
            adjacentButton.setOpaque(true);
            adjacentButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JOptionPane.showMessageDialog(window, "Adjacent seat has been reserved for wheelchair user.");
            return true; // Adjacent seat successful
        } else {
            JOptionPane.showMessageDialog(window, "This adjacent seat is invalid.");
            return false; // Adjacent seat failed
        }
    }


    // Method to check if an adjacent seat is valid for removal
    private boolean isValidAdjacentSeat(int row, int col, boolean isLeft, JButton[][] buttons) {
        int targetCol = isLeft ? col - 1 : col + 1; // checks if we want to go left or right

        // Check if the target column is within bounds
        if (targetCol < 0 || targetCol >= BUTTONS_PER_ROW) {
            return false; // Out of bounds
        }

        // Check if the adjacent seat is available
        JButton adjacentButton = buttons[row][targetCol];
        return adjacentButton.isEnabled(); // Ensures it's not already booked or reserved
    }

    // Method to revert seat status
    private void revertSeatStatus(String seatId, JButton seatButton) {
        seats.put(seatId, SeatStatus.AVAILABLE);
        seatButton.setEnabled(true);
        seatButton.setText(seatId); // Restores the text
        seatButton.setBackground(Color.WHITE);
        seatButton.setOpaque(true);
        seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JOptionPane.showMessageDialog(window, seatId + " is now available again.");
    }

    private void showRefundOption() {
        String seatId = JOptionPane.showInputDialog(window, "Enter the seat ID to refund (e.g.S 1 or B 1):");
        if (seatId != null && !seatId.trim().isEmpty()) {
            seatId = seatId.trim();
            if (seatId.startsWith("S ") || seatId.startsWith("B ")) {
                JButton seatButton = findSeatButton(seatId);
                if (seatButton != null) {
                    //System.out.print("nice it worked");
                    refundSeat(seatId, seatButton);

                } else {
                    JOptionPane.showMessageDialog(window, "Seat ID not found.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid seat ID format. Please use 'S ' or 'B ' followed by a space and the seat number.");
            }
        }
    }

    private JButton findSeatButton(String seatId) {
        JButton[][] buttonsArray = seatId.startsWith("S ") ? stallButtons : balconyButtons;
        int seatNumber = Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1;
        int row = seatNumber / BUTTONS_PER_ROW;
        int col = seatNumber % BUTTONS_PER_ROW;
    
        if (row < buttonsArray.length && col < buttonsArray[row].length) {
            return buttonsArray[row][col];
        }
        return null;
    }
    
    private void refundSeat(String seatId, JButton seatButton) {
        if (seats.getOrDefault(seatId, SeatStatus.AVAILABLE) != SeatStatus.AVAILABLE) {
            seats.put(seatId, SeatStatus.AVAILABLE);
            seatButton.setEnabled(true);
            seatButton.setText(seatId); // Restores the text
            seatButton.setBackground(Color.WHITE);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JOptionPane.showMessageDialog(window, seatId + " has been refunded and is now available.");
            deleteSeatBooking(seatId);
        } else {
            JOptionPane.showMessageDialog(window, seatId + " is already available.");
        }
    }   
    
    private int getLatestShowId() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT show_id FROM Shows ORDER BY show_id DESC LIMIT 1")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("show_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getLatestRoomID() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT room_id FROM Rooms WHERE room_name = 'Main Hall' LIMIT 1")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("room_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }


    private int getLatestBookingId() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT booking_id FROM BookingRoom WHERE show_id = ? AND room_id = ? ORDER BY booking_id LIMIT 1")) {
                    pstmt.setInt(1, showId);
                    pstmt.setInt(2, roomId);
                    ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("booking_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void saveSeatBooking(String seatId, SeatStatus status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO SeatStates (show_id, seat_number, status, room_id) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, showId);
            pstmt.setString(2, seatId);
            pstmt.setString(3, status.name());
            pstmt.setInt(4, roomId); 
            pstmt.executeUpdate();

        if (status == SeatStatus.BOOKED || status == SeatStatus.RESERVED || status == SeatStatus.WHEELCHAIR) {
            try (PreparedStatement pstmt1 = conn.prepareStatement(
                "INSERT INTO Tickets (booking_id,seat_number, price) VALUES (?, ?, ?)")) {
                pstmt1.setInt(1, BookingId);
                pstmt1.setString(2, seatId);
                pstmt1.setDouble(3, 6.50); 
                pstmt1.executeUpdate();
            }
        }
            System.out.println("Saved booking for seat: " + seatId + " with status: " + status);
        } catch (SQLException e) {
            System.err.println("Error saving seat booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSeatBooking(String seatId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM SeatStates WHERE show_id = ? AND seat_number = ? AND room_id = ?")) {
            pstmt.setInt(1, showId);
            pstmt.setString(2, seatId);
            pstmt.setInt(3, roomId); 
            pstmt.executeUpdate();


        try (PreparedStatement pstmt1 = conn.prepareStatement(
            "DELETE FROM SeatStates WHERE show_id = ? AND seat_number = ? AND room_id = ?, AND boking_id = ?")) {
            pstmt1.setInt(1, showId);
            pstmt1.setString(2, seatId);
            pstmt1.setInt(3, roomId);
            pstmt1.setInt(4, BookingId);
            pstmt1.executeUpdate();
        }
            System.out.println("Deleted booking for seat: " + seatId);
        } catch (SQLException e) {
            System.err.println("Error deleting seat booking: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
}
