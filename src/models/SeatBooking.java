package models;
import javax.swing.*;
import javax.swing.border.Border;

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
  
    private enum SeatStatus {AVAILABLE, BOOKED, RESERVED, WHEELCHAIR}

    private Sidebar sidebar; 
    private JFrame window;


    private static final int BUTTONS_PER_ROW = 20;
    private static final int S_WIDTH = 100;
    private static final int S_HEIGHT = 65;
    private static final int B_WIDTH = (int) (S_WIDTH * 0.9);
    private static final int B_HEIGHT = S_HEIGHT;

    private int showId;
    private int roomId;
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
            button.setEnabled(false);
            button.setText(seatId);
            button.setBackground(
                    status == SeatStatus.WHEELCHAIR ? new Color(102, 178, 255) :
                            status == SeatStatus.RESERVED ? new Color(144, 238, 144) :
                                    new Color(255, 204, 153)
            );
            button.setForeground(new Color(46, 83, 63));
            button.setOpaque(true);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        });
    }

    private void initialiseGui(JFrame window) {
        // Creates a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(163, 204, 167));


        // Creates the menu button
        sidebar=new Sidebar();
        mainPanel.add(sidebar.getSidebar(), BorderLayout.WEST);
        JButton menuButton = createMenuButton();

        // Combining the menu button and screen button into a single panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(163, 204, 167));

        // Menu button aligned to the left
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(new Color(163, 204, 167));
        menuPanel.add(menuButton);
        topPanel.add(menuPanel, BorderLayout.WEST);

        JPanel stallsPanel = createSeatPanel(285, "S ", S_WIDTH, S_HEIGHT);
        Dimension stallsSize = stallsPanel.getPreferredSize();
        stallsPanel.setPreferredSize(stallsSize);
        stallsPanel.setMinimumSize(stallsSize);
        stallsPanel.setMaximumSize(stallsSize);

        JScrollPane scrollableStalls = new JScrollPane(stallsPanel);
        scrollableStalls.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollableStalls.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollableStalls.setBorder(null);
        scrollableStalls.getViewport().setBackground(new Color(163, 204, 167));
        scrollableStalls.getVerticalScrollBar().setUnitIncrement(16);
        scrollableStalls.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        scrollableStalls.setPreferredSize(new Dimension(stallsSize.width + 20, 300));

        JPanel balconyPanel = createSeatPanel(89, "B ", B_WIDTH, B_HEIGHT);



        JPanel seatPanels = new JPanel();
        seatPanels.setLayout(new BoxLayout(seatPanels, BoxLayout.Y_AXIS));
        seatPanels.setOpaque(false);
        seatPanels.add(scrollableStalls);
        seatPanels.add(Box.createVerticalStrut(10));
        seatPanels.add(balconyPanel);


        JPanel centerWrapper = new JPanel(new BorderLayout());
       centerWrapper.setBackground(new Color(163, 204, 167));
       centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
       centerWrapper.add(seatPanels, BorderLayout.CENTER);
       mainPanel.add(centerWrapper, BorderLayout.CENTER);

       JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       legendPanel.setBackground(new Color(46, 83, 63));
       legendPanel.setOpaque(true);
       legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
       legendPanel.add(createLegendItem("Available", new Color(181, 222, 184)));
       legendPanel.add(createLegendItem("Booked", new Color(255, 204, 153)));
       legendPanel.add(createLegendItem("Reserved", new Color(144, 238, 144)));
       legendPanel.add(createLegendItem("Wheelchair", new Color(102, 178, 255)));
       mainPanel.add(legendPanel, BorderLayout.SOUTH);
       
       window.setLayout(new BorderLayout());
       window.getContentPane().setBackground(new Color(163, 204, 167));
       window.add(mainPanel, BorderLayout.CENTER);
       window.revalidate();
        
    

        // Add the seat panels to the main panel

        // Add a refund button
        JButton refundButton = new JButton("Refund Seat");
        refundButton.setFont(new Font("Georgia", Font.PLAIN, 11));
        refundButton.setBackground(new Color(181, 222, 184));
        refundButton.setForeground(new Color(46, 83, 63));
        refundButton.setFocusPainted(false);
        refundButton.setPreferredSize(new Dimension(120, 50));
        refundButton.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        refundButton.setOpaque(false);
        refundButton.setContentAreaFilled(false);
        refundButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        refundButton.setBorder(new GlowingRoundedBorder(20, new Color(97, 143, 110)));
        refundButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refundButton.setOpaque(true);
                refundButton.setBackground(new Color(197, 234, 198));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refundButton.setOpaque(false);
            }
        });


        refundButton.addActionListener(e -> showRefundOption());
        menuPanel.add(refundButton);


        // Creates the screen panel
        JPanel screenPanel = createScreenPanel();
        JPanel screenWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        screenWrapper.setBackground(new Color(163, 204, 167));
        screenWrapper.add(screenPanel);
        topPanel.add(screenPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(163, 204, 167));
        JLabel title = new JLabel("Lancaster's Music Hall - Main Hall Booking");
        title.setFont(new Font("Georgia", Font.BOLD, 28));
        title.setForeground(new Color(46, 83, 63));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        



        // Adds the main panel to the window
        mainPanel.add(topPanel, BorderLayout.NORTH);
        window.add(mainPanel);
        window.revalidate(); 
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        item.setOpaque(false);
        JLabel colourBox = new JLabel();
        colourBox.setOpaque(true);
        colourBox.setBackground(color);
        colourBox.setPreferredSize(new Dimension(20, 20));
        colourBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        JLabel text = new JLabel(" " + label);
        text.setForeground(new Color(181, 222, 184));
        text.setFont(new Font("Georgia", Font.PLAIN, 12));
        item.add(colourBox);
        item.add(text);
        return item;
    }

    private JButton createMenuButton() {
        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("Georgia", Font.BOLD, 24));
        menuButton.setBackground(new Color(46, 83, 63));
        menuButton.setForeground(new Color(181, 222, 184));
        menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuButton.setFocusPainted(false);
        menuButton.setOpaque(true);
        menuButton.setContentAreaFilled(true);
        menuButton.addActionListener(e -> sidebar.toggleSidebar());
        return menuButton;
    }


    private JPanel createScreenPanel() {
         JPanel screenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        screenPanel.setBackground(new Color(181, 222, 184));
        JButton screenButton = new JButton("S  C  R  E  E  N");
        screenButton.setFont(new Font("Georgia", Font.BOLD, 20));
        screenButton.setForeground(new Color(46, 83, 63));
        screenButton.setBackground(new Color(163, 204, 167));
        screenButton.setBorder(BorderFactory.createLineBorder(new Color(46, 83, 63), 2));
        screenButton.setFocusPainted(false);
        screenButton.setOpaque(true);
        screenButton.setPreferredSize(new Dimension(600, 40));
        screenPanel.add(screenButton);

        return screenPanel;
    }

    private JPanel createSeatPanel(int numberOfSeats, String seatPrefix, int width, int height) {
        JPanel seatPanel = new JPanel(new GridBagLayout());
        seatPanel.setBackground(new Color(163, 204, 167));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);


        int fullRows = numberOfSeats / BUTTONS_PER_ROW;
        int remainingSeats = numberOfSeats % BUTTONS_PER_ROW;
        int totalRows = fullRows + (remainingSeats > 0 ? 1 : 0);


        JButton[][] buttons = new JButton[totalRows][];


        for (int row = 0; row < fullRows; row++) {
            buttons[row] = new JButton[BUTTONS_PER_ROW];
            for (int col = 0; col < BUTTONS_PER_ROW; col++) {
                int seatNum = (row * BUTTONS_PER_ROW) + col + 1;
                String seatId = seatPrefix + seatNum;
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                buttons[row][col] = seatButton;


                gbc.gridx = col;
                gbc.gridy = row;
                seatPanel.add(seatButton, gbc);
            }
        }


        if (remainingSeats > 0) {
            buttons[totalRows - 1] = new JButton[remainingSeats];
            int startCol = (BUTTONS_PER_ROW - remainingSeats) / 2;
            for (int col = 0; col < remainingSeats; col++) {
                int seatNum = (fullRows * BUTTONS_PER_ROW) + col + 1;
                String seatId = seatPrefix + seatNum;
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                buttons[totalRows - 1][col] = seatButton;


                gbc.gridx = startCol + col;
                gbc.gridy = totalRows - 1;
                seatPanel.add(seatButton, gbc);
            }
        }


        if (seatPrefix.equals("S ")) {
            stallButtons = buttons;
        } else {
            balconyButtons = buttons;
        }


        return seatPanel;

      
    }
    private JButton createStyledSeatButton(String seatId, int width, int height) {
        JButton seatButton = new JButton(seatId);
        seatButton.setPreferredSize(new Dimension(width, height));
        seatButton.setFont(new Font("Georgia", Font.PLAIN, 16));
        seatButton.setEnabled(true);
        seatButton.setToolTipText("Seat " + seatId);
        seatButton.setOpaque(true);
        seatButton.setBackground(new Color(181, 222, 184));
        seatButton.setForeground(new Color(46, 83, 63));
        seatButton.setBorder(new SimpleRoundedBorder(15, new Color(46, 83, 63)));


        seatButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (seatButton.isEnabled()) {
                    seatButton.setBorder(new GlowingRoundedBorder(15, new Color(97, 143, 110)));
                }
            }


            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (seatButton.isEnabled()) {
                    seatButton.setBorder(new SimpleRoundedBorder(15, new Color(46, 83, 63)));
                }
            }
        });


        seatButton.addActionListener(e -> showSeatOptions(seatId, seatButton));
        return seatButton;
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
            case 0 -> updateSeatStatus(seatId, seatButton, SeatStatus.BOOKED);
            case 1 -> updateSeatStatus(seatId, seatButton, SeatStatus.RESERVED);
            case 2 -> {
                updateSeatStatus(seatId, seatButton, SeatStatus.WHEELCHAIR);
                int seatNum = Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1;
                int row = seatNum / BUTTONS_PER_ROW;
                int col = seatNum % BUTTONS_PER_ROW;
                JButton[][] targetButtons = seatId.startsWith("S ") ? stallButtons : balconyButtons;
                if (!offerAdjacentSeatChoice(row, col, targetButtons)) {
                    revertSeatStatus(seatId, seatButton);
                }
            }
        }
    }


    private void updateSeatStatus(String seatId, JButton seatButton, SeatStatus status) {
        if (seats.getOrDefault(seatId, SeatStatus.AVAILABLE) == SeatStatus.AVAILABLE) {
            seats.put(seatId, status);
            seatButton.setEnabled(false);
            seatButton.setText(seatId);
            seatButton.setBackground(
                    status == SeatStatus.WHEELCHAIR ? new Color(102, 178, 255) :
                            status == SeatStatus.RESERVED ? new Color(144, 238, 144) :
                                    new Color(255, 204, 153)
            );
            seatButton.setForeground(new Color(46, 83, 63));
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JOptionPane.showMessageDialog(window, seatId + " is now " + status + ".");
            saveSeatBooking(seatId, status); 
        } else {
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

            // If it was a wheelchair seat, reset the adjacent gray seat
            SeatStatus currentStatus = seats.get(seatId);
            if (currentStatus != null && currentStatus != SeatStatus.AVAILABLE) {
                if (currentStatus == SeatStatus.WHEELCHAIR) {
            // Calculate row and column
            int seatNumber = Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1;
            int row = seatNumber / BUTTONS_PER_ROW;
            int col = seatNumber % BUTTONS_PER_ROW;
            JButton[][] buttons = seatId.startsWith("S ") ? stallButtons : balconyButtons;

            // Checks both left and right adjacent seats
            if (col > 0 && buttons[row][col-1].getBackground().equals(Color.GRAY)) {
                resetAdjacentSeat(buttons[row][col-1], seatId.startsWith("S ") ? "S " : "B ", row, col-1);
            }
            if (col < BUTTONS_PER_ROW-1 && buttons[row][col+1].getBackground().equals(Color.GRAY)) {
                resetAdjacentSeat(buttons[row][col+1], seatId.startsWith("S ") ? "S " : "B ", row, col+1);
            }
        }
        seats.put(seatId, SeatStatus.AVAILABLE);
            seatButton.setEnabled(true);
            seatButton.setText(seatId); // Restores the text
            seatButton.setBackground(new Color(181, 222, 184));
            seatButton.setForeground(new Color(46, 83, 63));            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            


            JOptionPane.showMessageDialog(window, seatId + " has been refunded and is now available.");
            deleteSeatBooking(seatId);
        } else {
            JOptionPane.showMessageDialog(window, seatId + " is already available.");
        }
    }  
    
    private void resetAdjacentSeat(JButton button, String prefix, int row, int col) {
        String adjacentSeatId = prefix + ((row * BUTTONS_PER_ROW + col) + 1);
        button.setEnabled(true);
        button.setText(adjacentSeatId);
        button.setBackground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        seats.put(adjacentSeatId, SeatStatus.AVAILABLE);
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
                "DELETE FROM Tickets WHERE booking_id = ? AND seat_number = ?")) {
            pstmt1.setInt(1, BookingId);
            pstmt1.setString(2, seatId);
            pstmt1.executeUpdate();
        }

         

        
            System.out.println("Deleted booking for seat: " + seatId);
        } catch (SQLException e) {
            System.err.println("Error deleting seat booking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class GlowingRoundedBorder implements Border {
        private final int radius;
        private final Color glowColor;

        public GlowingRoundedBorder(int radius, Color glowColor) {
            this.radius = radius;
            this.glowColor = glowColor;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 4, radius + 4, radius + 4, radius + 4);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 100));
            g2.setStroke(new BasicStroke(6f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);

            g2.setColor(glowColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class SimpleRoundedBorder implements Border {
        private final int radius;
        private final Color color;

        public SimpleRoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(3.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
    
    
    
}
