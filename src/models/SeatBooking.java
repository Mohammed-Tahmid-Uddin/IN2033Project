package models;
import javax.swing.*;
import javax.swing.border.Border;

import Componenets.Sidebar;
import utils.CustomerManager;
import utils.DatabaseConnection;
import utils.VerticalLabelUI;

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

    private java.util.List<String> selectedSeats = new java.util.ArrayList<>();
    private boolean isGroupBookingMode = false;
    private JButton groupBookingButton;

    private enum SeatStatus {AVAILABLE, BOOKED, RESERVED, WHEELCHAIR}

    private Sidebar sidebar;
    private JFrame window;


    private static final int BUTTONS_PER_ROW = 20;
    private static final int S_WIDTH = 32;  // Same as in createStyledSeatButton
    private static final int S_HEIGHT = 32;

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
            if (button != null) { // Ensure the button exists
                button.setEnabled(false);
                button.setText(seatId.replaceAll("[A-Z]+", "")); // Show only the seat number (e.g., "1")
                button.setBackground(
                        status == SeatStatus.WHEELCHAIR ? new Color(102, 178, 255) :
                                status == SeatStatus.RESERVED ? new Color(144, 238, 144) :
                                        new Color(255, 204, 153)
                );
                button.setForeground(new Color(46, 83, 63));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        });
    }

    // Remove the createScreenPanel method (delete the following method entirely)
// private JPanel createScreenPanel() { ... }

    private void initialiseGui(JFrame window) {
        // Creates a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(163, 204, 167));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Match reference

        // Creates the menu button
        sidebar = new Sidebar();
        mainPanel.add(sidebar.getSidebar(), BorderLayout.WEST);
        JButton menuButton = createMenuButton();

        // Combining the menu button and other buttons into a single panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(163, 204, 167));

        // Menu button aligned to the left
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(new Color(163, 204, 167));
        menuPanel.add(menuButton);

        // Add a refund seat button
        JButton refundSeatButton = new JButton("Refund Seat");
        refundSeatButton.setFont(new Font("Georgia", Font.PLAIN, 10));
        refundSeatButton.setBackground(new Color(181, 222, 184));
        refundSeatButton.setForeground(new Color(46, 83, 63));
        refundSeatButton.setFocusPainted(false);
        refundSeatButton.setPreferredSize(new Dimension(100, 40));
        refundSeatButton.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        refundSeatButton.setOpaque(false);
        refundSeatButton.setContentAreaFilled(false);
        refundSeatButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        refundSeatButton.setBorder(new GlowingRoundedBorder(15, new Color(97, 143, 110)));
        refundSeatButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refundSeatButton.setOpaque(true);
                refundSeatButton.setBackground(new Color(197, 234, 198));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refundSeatButton.setOpaque(false);
            }
        });
        refundSeatButton.addActionListener(e -> showRefundSeatOption());
        menuPanel.add(refundSeatButton);

        // Add a refund room button
        JButton refundRoomButton = new JButton("Refund Room");
        refundRoomButton.setFont(new Font("Georgia", Font.PLAIN, 10));
        refundRoomButton.setBackground(new Color(181, 222, 184));
        refundRoomButton.setForeground(new Color(46, 83, 63));
        refundRoomButton.setFocusPainted(false);
        refundRoomButton.setPreferredSize(new Dimension(100, 40));
        refundSeatButton.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        refundRoomButton.setOpaque(false);
        refundRoomButton.setContentAreaFilled(false);
        refundRoomButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        refundRoomButton.setBorder(new GlowingRoundedBorder(15, new Color(97, 143, 110)));
        refundRoomButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refundRoomButton.setOpaque(true);
                refundRoomButton.setBackground(new Color(197, 234, 198));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refundRoomButton.setOpaque(false);
            }
        });
        refundRoomButton.addActionListener(e -> showRefundRoomOption());
        menuPanel.add(refundRoomButton);

        // Add a group booking button
        groupBookingButton = new JButton("Start Group Booking");
        groupBookingButton.setFont(new Font("Georgia", Font.PLAIN, 10));
        groupBookingButton.setBackground(new Color(181, 222, 184));
        groupBookingButton.setForeground(new Color(46, 83, 63));
        groupBookingButton.setFocusPainted(false);
        groupBookingButton.setPreferredSize(new Dimension(120, 40));
        groupBookingButton.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        groupBookingButton.setOpaque(false);
        groupBookingButton.setContentAreaFilled(false);
        groupBookingButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        groupBookingButton.setBorder(new GlowingRoundedBorder(15, new Color(97, 143, 110)));
        groupBookingButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                groupBookingButton.setOpaque(true);
                groupBookingButton.setBackground(new Color(197, 234, 198));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                groupBookingButton.setOpaque(false);
            }
        });
        groupBookingButton.addActionListener(e -> toggleGroupBookingMode());
        menuPanel.add(groupBookingButton);

        topPanel.add(menuPanel, BorderLayout.WEST);

        // Header panel with title and menu
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(163, 204, 167));
        JLabel title = new JLabel("Lancaster's Music Hall - Main Hall Booking");
        title.setFont(new Font("Georgia", Font.BOLD, 24));
        title.setForeground(new Color(46, 83, 63));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        // Create the top balcony (33 seats)
        JPanel topWrapper = new JPanel(new BorderLayout());
        JPanel topBalconyPanel = createSeatPanel(33, "B ", S_WIDTH, S_HEIGHT); // Use same size for all seats
        topBalconyPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 140)); // Ensure preferred height
        topWrapper.add(topBalconyPanel, BorderLayout.NORTH);

        // Combine header and top balcony into a single north panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(new Color(163, 204, 167));
        northPanel.add(headerPanel);
        northPanel.add(topWrapper);
        northPanel.add(Box.createVerticalStrut(10)); // Add spacing between header and top balcony
        mainPanel.add(northPanel, BorderLayout.NORTH);

        // Center area with side balconies and stalls
        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        centerPanel.setBackground(new Color(163, 204, 167)); // Set background
        centerPanel.setOpaque(true); // Ensure not transparent
        centerPanel.add(createSeatPanel(25, "BL ", S_WIDTH, S_HEIGHT), BorderLayout.WEST); // Use same size for all seats
        JPanel stallsPanel = createSeatPanel(285, "S ", S_WIDTH, S_HEIGHT);
        stallsPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, stallsPanel.getPreferredSize().height)); // Maximize width
        centerPanel.add(stallsPanel, BorderLayout.CENTER);
        centerPanel.add(createSeatPanel(27, "BR ", S_WIDTH, S_HEIGHT), BorderLayout.EAST); // Use same size for all seats
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add Stage at Bottom (original implementation)
        JLabel stageLabel = new JLabel("STAGE", SwingConstants.CENTER);
        stageLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        stageLabel.setForeground(new Color(46, 83, 63));
        stageLabel.setBackground(Color.LIGHT_GRAY);
        stageLabel.setOpaque(true);
        stageLabel.setBorder(BorderFactory.createLineBorder(new Color(46, 83, 63), 2));
        // Calculate width: seats 3-17 = 15 seats * S_WIDTH + spacing
        int stageWidth = 15 * S_WIDTH + 14 * 1; // 15 seats + 14 gaps of 1 pixel
        stageLabel.setPreferredSize(new Dimension(stageWidth, 30));
        mainPanel.add(stageLabel, BorderLayout.SOUTH);

        // Add legend panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(new Color(46, 83, 63));
        legendPanel.setOpaque(true);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        legendPanel.add(createLegendItem("Available", new Color(181, 222, 184)));
        legendPanel.add(createLegendItem("Booked", new Color(255, 204, 153)));
        legendPanel.add(createLegendItem("Reserved", new Color(144, 238, 144)));
        legendPanel.add(createLegendItem("Wheelchair", new Color(102, 178, 255)));
        mainPanel.add(legendPanel, BorderLayout.SOUTH);

        window.setLayout(new BorderLayout());
        window.getContentPane().setBackground(new Color(163, 204, 167));
        window.add(mainPanel, BorderLayout.CENTER);
        window.revalidate();
    }
    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        item.setOpaque(false);
        JLabel colourBox = new JLabel();
        colourBox.setOpaque(true);
        colourBox.setBackground(color);
        colourBox.setPreferredSize(new Dimension(15, 15)); // Reduced size
        colourBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        JLabel text = new JLabel(" " + label);
        text.setForeground(new Color(181, 222, 184));
        text.setFont(new Font("Georgia", Font.PLAIN, 10)); // Reduced font size
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



    private JPanel createSeatPanel(int numberOfSeats, String seatPrefix, int width, int height) {
        JPanel seatPanel = new JPanel();
        seatPanel.setBackground(new Color(163, 204, 167));
        seatPanel.setOpaque(true); // Ensure panel is not transparent

        if (seatPrefix.equals("S ")) { // Stalls
            seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
            seatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Stalls layout: 285 seats
            // Rows A-L: 19 seats each (228 seats)
            // Row M: 16 seats (1-8, 11-18) (16 seats)
            // Row N: 19 seats (1-9, 11-19) (19 seats)
            // Row O: 20 seats (1-10, 11-20) (20 seats)
            // Row P: 11 seats (1-5, 12-17) (11 seats)
            // Row Q: 10 seats (1-5, 13-17) (10 seats)
            int[] seatsPerRow = new int[]{10, 11, 20, 19, 16, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19}; // Q to A
            int totalRows = seatsPerRow.length;
            stallButtons = new JButton[totalRows][];

            // Order rows from Q to A (bottom to top)
            String[] rowLabels = {"Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"};
            for (int row = 0; row < totalRows; row++) {
                int seatsInThisRow = seatsPerRow[row];
                stallButtons[row] = new JButton[seatsInThisRow];

                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
                rowPanel.setBackground(new Color(163, 204, 167)); // Set background
                rowPanel.setOpaque(true); // Ensure not transparent

                // Add "STALLS" labels on the left and right for rows P and Q
                if (rowLabels[row].equals("Q") || rowLabels[row].equals("P")) {
                    JLabel stallsLeftLabel = new JLabel("STALLS");
                    stallsLeftLabel.setFont(new Font("Georgia", Font.BOLD, 12));
                    stallsLeftLabel.setForeground(new Color(46, 83, 63));
                    rowPanel.add(stallsLeftLabel);
                }

                // Add seats with larger gaps between columns 13-14, 14-15, 15-16
                for (int col = 0; col < seatsInThisRow; col++) {
                    int seatNum;
                    if (row <= 4) { // Rows Q to M (bottom to top)
                        if (col < seatsInThisRow / 2) {
                            seatNum = col + 1; // Left side (e.g., 1-5 for Row Q)
                        } else {
                            // Adjust numbering for the right side
                            if (row == 0) { // Row Q: 13-17
                                seatNum = col + 8;
                            } else if (row == 1) { // Row P: 12-17
                                seatNum = col + 7;
                            } else if (row == 2) { // Row O: 11-20
                                seatNum = col + 1;
                            } else if (row == 3) { // Row N: 11-19
                                seatNum = col + 2;
                            } else { // Row M: 11-18
                                seatNum = col + 3;
                            }
                        }
                    } else {
                        seatNum = col + 1; // Rows L to A: 1-19
                    }
                    String seatId = rowLabels[row] + seatNum; // Simplified seat ID (e.g., "A1", "Q13")
                    JButton seatButton = createStyledSeatButton(seatId, width, height);
                    seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "1")
                    stallButtons[row][col] = seatButton;

                    // Add larger gaps before seats 14, 15, and 16
                    if (seatNum == 14 || seatNum == 15 || seatNum == 16) {
                        rowPanel.add(Box.createHorizontalStrut(10)); // Larger gap
                    }
                    // Add a central aisle for rows M-Q
                    if (row <= 4 && col == seatsInThisRow / 2) {
                        rowPanel.add(Box.createHorizontalStrut(10)); // Central aisle
                    }

                    rowPanel.add(seatButton);
                }

                // Add "STALLS" labels on the right for rows P and Q
                if (rowLabels[row].equals("Q") || rowLabels[row].equals("P")) {
                    JLabel stallsRightLabel = new JLabel("STALLS");
                    stallsRightLabel.setFont(new Font("Georgia", Font.BOLD, 12));
                    stallsRightLabel.setForeground(new Color(46, 83, 63));
                    rowPanel.add(stallsRightLabel);
                }

                // Add row label on the right
                JLabel rightRowLabel = new JLabel(rowLabels[row]);
                rightRowLabel.setFont(new Font("Georgia", Font.PLAIN, 10));
                rightRowLabel.setForeground(new Color(46, 83, 63));
                rowPanel.add(rightRowLabel);

                seatPanel.add(rowPanel);
            }

        } else if (seatPrefix.equals("B ")) { // Top Balcony
            // Top Balcony layout: 33 seats
            // Row CC: 1-8 (8 seats)
            // Row BB: 9-23 (15 seats)
            // Row AA: 24-33 (10 seats)
            seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
            seatPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Match reference
            seatPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 140)); // Ensure preferred height

            int[] seatsPerRow = new int[]{10, 15, 8}; // AA, BB, CC
            int totalRows = seatsPerRow.length;
            balconyButtons = new JButton[totalRows][];

            String[] rowLabels = {"AA", "BB", "CC"};
            int[] seatStarts = {24, 9, 1}; // Starting seat numbers for AA, BB, CC
            for (int row = 0; row < totalRows; row++) {
                int seatsInThisRow = seatsPerRow[row];
                balconyButtons[row] = new JButton[seatsInThisRow];

                // Add row label
                JLabel rowLabel = new JLabel("BALCONY - Row " + rowLabels[row] + " (" + seatStarts[row] + "-" + (seatStarts[row] + seatsInThisRow - 1) + ")");
                rowLabel.setFont(new Font("Georgia", Font.PLAIN, 12));
                rowLabel.setForeground(new Color(46, 83, 63));
                seatPanel.add(rowLabel);

                // Add seats
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
                rowPanel.setBackground(new Color(163, 204, 167)); // Set background
                rowPanel.setOpaque(true); // Ensure not transparent
                for (int col = 0; col < seatsInThisRow; col++) {
                    int seatNum = seatStarts[row] + col;
                    String seatId = rowLabels[row] + seatNum; // Simplified seat ID (e.g., "AA24", "BB9", "CC1")
                    JButton seatButton = createStyledSeatButton(seatId, width, height);
                    seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "24")
                    balconyButtons[row][col] = seatButton;
                    rowPanel.add(seatButton);
                }
                seatPanel.add(rowPanel);
            }
        } else if (seatPrefix.equals("BL ")) { // Left Side Balcony
            // Left Side Balcony: 25 seats
            // Row AA: 1-20 (20 seats)
            // Row BB: 1-5 (5 seats)
            // Display as two vertical columns
            seatPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(1, 1, 1, 1);

            int totalRows = 20; // Height matches AA (20 seats)
            JButton[][] buttons = new JButton[totalRows][2]; // Two columns

            // Column 1: AA 1-20
            for (int row = 0; row < 20; row++) {
                String rowLabelText = "AA";
                int seatNum = row + 1;
                String seatId = rowLabelText + seatNum; // Simplified seat ID (e.g., "AA1")
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "1")
                buttons[row][0] = seatButton;

                // Add row label on the left for AA
                if (row == 0) {
                    JLabel leftRowLabel = new JLabel(rowLabelText);
                    leftRowLabel.setFont(new Font("Georgia", Font.PLAIN, 10));
                    leftRowLabel.setForeground(new Color(46, 83, 63));
                    gbc.gridx = 0;
                    gbc.gridy = row;
                    seatPanel.add(leftRowLabel, gbc);
                }

                gbc.gridx = 1;
                gbc.gridy = row;
                seatPanel.add(seatButton, gbc);
            }

            // Column 2: BB 1-5 (aligned with top 5 rows of AA)
            for (int row = 0; row < 5; row++) {
                String rowLabelText = "BB";
                int seatNum = row + 1;
                String seatId = rowLabelText + seatNum; // Simplified seat ID (e.g., "BB1")
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "1")
                buttons[row][1] = seatButton;

                // Add row label on the left for BB
                if (row == 0) {
                    JLabel leftRowLabel = new JLabel(rowLabelText);
                    leftRowLabel.setFont(new Font("Georgia", Font.PLAIN, 10));
                    leftRowLabel.setForeground(new Color(46, 83, 63));
                    gbc.gridx = 0;
                    gbc.gridy = row + 20; // Position BB label below AA
                    seatPanel.add(leftRowLabel, gbc);
                }

                gbc.gridx = 2;
                gbc.gridy = row;
                seatPanel.add(seatButton, gbc);
            }

            // Add "BALCONY" label vertically on the left
            JLabel balconyLabel = new JLabel("BALCONY");
            balconyLabel.setFont(new Font("Georgia", Font.BOLD, 12));
            balconyLabel.setForeground(new Color(46, 83, 63));
            balconyLabel.setUI(new VerticalLabelUI(true)); // Rotate the label vertically
            gbc.gridx = -1;
            gbc.gridy = totalRows / 2;
            seatPanel.add(balconyLabel, gbc);
        } else if (seatPrefix.equals("BR ")) { // Right Side Balcony
            // Right Side Balcony: 27 seats
            // Row AA: 1-20 (20 seats)
            // Row BB: 1-7 (7 seats)
            // Display as two vertical columns
            seatPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(1, 1, 1, 1);

            int totalRows = 20; // Height matches AA (20 seats)
            JButton[][] buttons = new JButton[totalRows][2]; // Two columns

            // Column 1: AA 1-20
            for (int row = 0; row < 20; row++) {
                String rowLabelText = "AA";
                int seatNum = row + 1;
                String seatId = rowLabelText + seatNum; // Simplified seat ID (e.g., "AA1")
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "1")
                buttons[row][0] = seatButton;

                gbc.gridx = 0;
                gbc.gridy = row;
                seatPanel.add(seatButton, gbc);

                // Add row label on the right for AA
                if (row == 0) {
                    JLabel rightRowLabel = new JLabel(rowLabelText);
                    rightRowLabel.setFont(new Font("Georgia", Font.PLAIN, 10));
                    rightRowLabel.setForeground(new Color(46, 83, 63));
                    gbc.gridx = 3;
                    gbc.gridy = row;
                    seatPanel.add(rightRowLabel, gbc);
                }
            }

            // Column 2: BB 1-7 (aligned with top 7 rows of AA)
            for (int row = 0; row < 7; row++) {
                String rowLabelText = "BB";
                int seatNum = row + 1;
                String seatId = rowLabelText + seatNum; // Simplified seat ID (e.g., "BB1")
                JButton seatButton = createStyledSeatButton(seatId, width, height);
                seatButton.setText(String.valueOf(seatNum)); // Show only the seat number (e.g., "1")
                buttons[row][1] = seatButton;

                gbc.gridx = 1;
                gbc.gridy = row;
                seatPanel.add(seatButton, gbc);

                // Add row label on the right for BB
                if (row == 0) {
                    JLabel rightRowLabel = new JLabel(rowLabelText);
                    rightRowLabel.setFont(new Font("Georgia", Font.PLAIN, 10));
                    rightRowLabel.setForeground(new Color(46, 83, 63));
                    gbc.gridx = 3;
                    gbc.gridy = row + 20; // Position BB label below AA
                    seatPanel.add(rightRowLabel, gbc);
                }
            }

            // Add "BALCONY" label vertically on the right
            JLabel balconyLabel = new JLabel("BALCONY");
            balconyLabel.setFont(new Font("Georgia", Font.BOLD, 12));
            balconyLabel.setForeground(new Color(46, 83, 63));
            balconyLabel.setUI(new VerticalLabelUI(false)); // Rotate the label vertically
            gbc.gridx = 4;
            gbc.gridy = totalRows / 2;
            seatPanel.add(balconyLabel, gbc);
        }

        return seatPanel;
    }
    private JButton createStyledSeatButton(String seatId, int width, int height) {
        JButton seatButton = new JButton(seatId);
        seatButton.setPreferredSize(new Dimension(32, 32)); // Match reference size
        seatButton.setFont(new Font("Georgia", Font.PLAIN, 12)); // Reduced font size
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

    private void toggleGroupBookingMode() {
        isGroupBookingMode = !isGroupBookingMode;
        if (isGroupBookingMode) {
            groupBookingButton.setText("Confirm Group Booking");
            selectedSeats.clear();
            JOptionPane.showMessageDialog(window, "Select seats for group booking. Click 'Confirm Group Booking' when done.");
        } else {
            groupBookingButton.setText("Start Group Booking");
            if (!selectedSeats.isEmpty()) {
                bookGroupSeats();
            } else {
                JOptionPane.showMessageDialog(window, "No seats selected for group booking.");
            }
        }
    }




    private void showSeatOptions(String seatId, JButton seatButton) {
        if (isGroupBookingMode) {
            if (seats.getOrDefault(seatId, SeatStatus.AVAILABLE) == SeatStatus.AVAILABLE) {
                if (selectedSeats.contains(seatId)) {
                    selectedSeats.remove(seatId);
                    seatButton.setBackground(new Color(181, 222, 184)); // Reset to available color
                } else {
                    selectedSeats.add(seatId);
                    seatButton.setBackground(Color.YELLOW); // Highlight selected seats
                }
                seatButton.setOpaque(true);
                seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            } else {
                JOptionPane.showMessageDialog(window, seatId + " is already taken.");
            }
        } else {
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
    }
    // For single seat bookings (prompts for customer)
    private void updateSeatStatus(String seatId, JButton seatButton, SeatStatus status) {
        // Prompt user to choose between Existing Customer, New Customer, or Cash Sale
        String[] options = {"Existing Customer", "New Customer", "Cash Sale"};
        int choice = JOptionPane.showOptionDialog(
                window,
                "Select customer type for this booking:",
                "Customer Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        Integer customerId = null;
        if (choice == 0) { // Existing Customer
            java.util.List<Customer> customers = CustomerManager.getAllCustomers();
            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(window, "No existing customers found. Please add a new customer.");
                return;
            }

            String[] customerNames = customers.stream()
                    .map(c -> c.getFirstName() + " " + c.getLastName() + " (ID: " + c.getCustomerId() + ")")
                    .toArray(String[]::new);
            String selectedCustomer = (String) JOptionPane.showInputDialog(
                    window,
                    "Select a customer:",
                    "Existing Customer",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    customerNames,
                    customerNames[0]
            );

            if (selectedCustomer != null) {
                String customerIdStr = selectedCustomer.substring(selectedCustomer.lastIndexOf(": ") + 2, selectedCustomer.length() - 1);
                customerId = Integer.parseInt(customerIdStr);
            } else {
                return; // User canceled
            }
        } else if (choice == 1) { // New Customer
            String firstName = JOptionPane.showInputDialog(window, "Enter first name:");
            if (firstName == null || firstName.trim().isEmpty()) return;

            String lastName = JOptionPane.showInputDialog(window, "Enter last name:");
            if (lastName == null || lastName.trim().isEmpty()) return;

            String email = JOptionPane.showInputDialog(window, "Enter email (optional):");
            String phone = JOptionPane.showInputDialog(window, "Enter phone (optional):");
            String isFriendStr = JOptionPane.showInputDialog(window, "Is this customer a Friend of Lancaster’s? (yes/no):");
            boolean isFriend = "yes".equalsIgnoreCase(isFriendStr);
            String isNhsStr = JOptionPane.showInputDialog(window, "Is this customer an NHS worker? (yes/no):");
            boolean isNhs = "yes".equalsIgnoreCase(isNhsStr);
            String isMilitaryStr = JOptionPane.showInputDialog(window, "Is this customer military personnel? (yes/no):");
            boolean isMilitary = "yes".equalsIgnoreCase(isMilitaryStr);

            Customer customer = CustomerManager.addCustomer(firstName, lastName, email, phone, isFriend, isNhs, isMilitary);
            if (customer != null) {
                customerId = customer.getCustomerId();
            } else {
                JOptionPane.showMessageDialog(window, "Failed to create new customer.");
                return;
            }
        }

        updateSeatStatus(seatId, seatButton, status, customerId, true); // Show message for single booking
    }

    // For group bookings (customerId is already determined)
    private void updateSeatStatus(String seatId, JButton seatButton, SeatStatus status, Integer customerId, boolean showMessage) {
        if (seats.getOrDefault(seatId, SeatStatus.AVAILABLE) == SeatStatus.AVAILABLE) {
            seats.put(seatId, status);
            seatButton.setEnabled(false);
            seatButton.setText(seatId.replaceAll("[A-Z]+", "")); // Show only the seat number (e.g., "1")
            seatButton.setBackground(
                    status == SeatStatus.WHEELCHAIR ? new Color(102, 178, 255) :
                            status == SeatStatus.RESERVED ? new Color(144, 238, 144) :
                                    new Color(255, 204, 153)
            );
            seatButton.setForeground(new Color(46, 83, 63));
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            if (showMessage) {
                JOptionPane.showMessageDialog(window, "Seat " + seatId + " is now " + status + ".");
            }
            saveSeatBooking(seatId, status, customerId);
        } else {
            if (showMessage) {
                JOptionPane.showMessageDialog(window, "Seat " + seatId + " is already taken.");
            }
        }
    }

    private void bookGroupSeats() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(window, "No seats selected for group booking.");
            return;
        }

        // Prompt user to choose between Existing Customer, New Customer, or Cash Sale (once for the group)
        String[] options = {"Existing Customer", "New Customer", "Cash Sale"};
        int choice = JOptionPane.showOptionDialog(
                window,
                "Select customer type for this group booking:",
                "Customer Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        Integer customerId = null;
        if (choice == 0) { // Existing Customer
            java.util.List<Customer> customers = CustomerManager.getAllCustomers();
            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(window, "No existing customers found. Please add a new customer.");
                return;
            }

            String[] customerNames = customers.stream()
                    .map(c -> c.getFirstName() + " " + c.getLastName() + " (ID: " + c.getCustomerId() + ")")
                    .toArray(String[]::new);
            String selectedCustomer = (String) JOptionPane.showInputDialog(
                    window,
                    "Select a customer:",
                    "Existing Customer",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    customerNames,
                    customerNames[0]
            );

            if (selectedCustomer != null) {
                String customerIdStr = selectedCustomer.substring(selectedCustomer.lastIndexOf(": ") + 2, selectedCustomer.length() - 1);
                customerId = Integer.parseInt(customerIdStr);
            } else {
                return; // User canceled
            }
        } else if (choice == 1) { // New Customer
            String firstName = JOptionPane.showInputDialog(window, "Enter first name:");
            if (firstName == null || firstName.trim().isEmpty()) return;

            String lastName = JOptionPane.showInputDialog(window, "Enter last name:");
            if (lastName == null || lastName.trim().isEmpty()) return;

            String email = JOptionPane.showInputDialog(window, "Enter email (optional):");
            String phone = JOptionPane.showInputDialog(window, "Enter phone (optional):");
            String isFriendStr = JOptionPane.showInputDialog(window, "Is this customer a Friend of Lancaster’s? (yes/no):");
            boolean isFriend = "yes".equalsIgnoreCase(isFriendStr);
            String isNhsStr = JOptionPane.showInputDialog(window, "Is this customer an NHS worker? (yes/no):");
            boolean isNhs = "yes".equalsIgnoreCase(isNhsStr);
            String isMilitaryStr = JOptionPane.showInputDialog(window, "Is this customer military personnel? (yes/no):");
            boolean isMilitary = "yes".equalsIgnoreCase(isMilitaryStr);

            Customer customer = CustomerManager.addCustomer(firstName, lastName, email, phone, isFriend, isNhs, isMilitary);
            if (customer != null) {
                customerId = customer.getCustomerId();
            } else {
                JOptionPane.showMessageDialog(window, "Failed to create new customer.");
                return;
            }
        }

        // Prompt for seat status
        String[] statusOptions = {"Book", "Reserve", "Wheelchair User"};
        String selectedStatus = (String) JOptionPane.showInputDialog(
                window,
                "Select status for the group booking:",
                "Seat Status",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]
        );

        if (selectedStatus == null) return; // User canceled

        SeatStatus status = selectedStatus.equals("Book") ? SeatStatus.BOOKED :
                selectedStatus.equals("Reserve") ? SeatStatus.RESERVED :
                        SeatStatus.WHEELCHAIR;

        // Book all selected seats
        for (String seatId : selectedSeats) {
            JButton seatButton = findSeatButton(seatId);
            if (seatButton != null) {
                updateSeatStatus(seatId, seatButton, status, customerId, false);
            }
        }

        JOptionPane.showMessageDialog(window, "Group booking completed for " + selectedSeats.size() + " seats.");
        selectedSeats.clear();
        groupBookingButton.setText("Start Group Booking");
        isGroupBookingMode = false;
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
    // Authorization method
    private boolean authorizeRefund() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Manager Authorization Required", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT role FROM Staff WHERE username = ? AND password = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if (role.equals("Deputy Manager") || role.equals("Manager")) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(window, "Only Deputy Managers or Managers can authorize refunds.");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid username or password.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error verifying authorization: " + e.getMessage());
            return false;
        }
    }

    private void showRefundSeatOption() {
        // Require authorization
        if (!authorizeRefund()) {
            return;
        }

        String seatId = JOptionPane.showInputDialog(window, "Enter the seat ID to refund (e.g., A1, AA24, BB1):");
        if (seatId != null && !seatId.trim().isEmpty()) {
            seatId = seatId.trim();
            if (seatId.matches("[A-Z]+\\d+")) { // Validate format (e.g., "A1", "AA24")
                JButton seatButton = findSeatButton(seatId);
                if (seatButton != null) {
                    refundSeat(seatId, seatButton);
                } else {
                    JOptionPane.showMessageDialog(window, "Seat ID not found.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "Invalid seat ID format. Please use a format like 'A1' or 'AA24'.");
            }
        }
    }

    private void showRefundRoomOption() {
        // Require authorization
        if (!authorizeRefund()) {
            return;
        }

        // List of other rooms (excluding Main Hall)
        String[] roomOptions = {
                "Rehearsal Space",
                "The Green Room",
                "Brontë Boardroom",
                "Dickens Den",
                "Poe Parlor",
                "Globe Room",
                "Chekhov Chamber"
        };
        String selectedRoom = (String) JOptionPane.showInputDialog(
                window,
                "Select a room to refund:",
                "Refund Room",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roomOptions,
                roomOptions[0]
        );

        if (selectedRoom == null) {
            return; // User canceled
        }

        // Check if the room has a booking on the specified date
        String bookingDate = "2025-04-05"; // Hardcoded for now; can be made dynamic later
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT booking_id FROM BookingRoom WHERE room_id = (SELECT room_id FROM Rooms WHERE room_name = ? LIMIT 1) AND booking_date = ?")) {
            pstmt.setString(1, selectedRoom);
            pstmt.setString(2, bookingDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int bookingId = rs.getInt("booking_id");

                // Confirm the refund
                int confirm = JOptionPane.showConfirmDialog(window,
                        "Are you sure you want to refund the booking for " + selectedRoom + " on " + bookingDate + "?",
                        "Confirm Room Refund",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // Delete the booking from BookingRoom
                try (PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM BookingRoom WHERE booking_id = ?")) {
                    deleteStmt.setInt(1, bookingId);
                    deleteStmt.executeUpdate();
                    JOptionPane.showMessageDialog(window, "Booking for " + selectedRoom + " on " + bookingDate + " has been refunded.");
                }
            } else {
                JOptionPane.showMessageDialog(window, "No booking found for " + selectedRoom + " on " + bookingDate + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error refunding room: " + e.getMessage());
        }
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
        JButton[][] buttonsArray;
        int row, col;

        // Extract row label and seat number from seatId (e.g., "A1" -> rowLabel="A", seatNum=1)
        String rowLabel;
        int seatNum;
        if (seatId.matches("[A-Z]+\\d+")) { // Matches "A1", "AA24", etc.
            // Find the index where the number starts
            int numberStartIndex = 0;
            for (int i = 0; i < seatId.length(); i++) {
                if (Character.isDigit(seatId.charAt(i))) {
                    numberStartIndex = i;
                    break;
                }
            }
            rowLabel = seatId.substring(0, numberStartIndex); // e.g., "A", "AA"
            seatNum = Integer.parseInt(seatId.substring(numberStartIndex)); // e.g., 1, 24
        } else {
            return null; // Invalid seat ID format
        }

        // Determine the section based on the row label
        if (rowLabel.matches("[A-Q]")) { // Stalls (A-Q)
            buttonsArray = stallButtons;
            // Map row label to index (Q to A)
            String[] rowLabels = {"Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"};
            row = -1;
            for (int i = 0; i < rowLabels.length; i++) {
                if (rowLabels[i].equals(rowLabel)) {
                    row = i;
                    break;
                }
            }
            col = seatNum - 1; // Seat numbers start at 1, array indices at 0
        } else if (rowLabel.equals("AA") || rowLabel.equals("BB") || rowLabel.equals("CC")) { // Top Balcony (AA, BB, CC)
            buttonsArray = balconyButtons;
            if (rowLabel.equals("AA")) {
                row = 0;
                col = seatNum - 24; // AA: 24-33
            } else if (rowLabel.equals("BB")) {
                row = 1;
                col = seatNum - 9; // BB: 9-23
            } else { // CC
                row = 2;
                col = seatNum - 1; // CC: 1-8
            }
        } else { // Side Balconies (AA, BB, but not top balcony)
            // Determine if it's left (BL) or right (BR) based on seat numbers
            if (seatId.startsWith("AA") && seatNum >= 1 && seatNum <= 20) {
                // AA seats are 1-20 for both BL and BR
                // We need to check the context, but since we can't distinguish BL/BR from seatId alone,
                // we'll try both and return the first non-null result
                // First try BL
                buttonsArray = new JButton[20][2]; // Two columns, 20 rows (height of AA)
                for (int r = 0; r < 20; r++) {
                    // Column 1: AA 1-20
                    String seatIdAA = "AA" + (r + 1);
                    buttonsArray[r][0] = createStyledSeatButton(seatIdAA, S_WIDTH, S_HEIGHT);
                    buttonsArray[r][0].setText(String.valueOf(r + 1));
                    // Column 2: BB 1-5 (rows 0-4)
                    if (r < 5) {
                        String seatIdBB = "BB" + (r + 1);
                        buttonsArray[r][1] = createStyledSeatButton(seatIdBB, S_WIDTH, S_HEIGHT);
                        buttonsArray[r][1].setText(String.valueOf(r + 1));
                    }
                }
                row = seatNum - 1; // AA: 1-20 (rows 0-19)
                col = 0; // Column 1
                if (row >= 0 && row < buttonsArray.length && col >= 0 && col < buttonsArray[row].length && buttonsArray[row][col] != null) {
                    return buttonsArray[row][col];
                }

                // If not found in BL, try BR
                buttonsArray = new JButton[20][2]; // Two columns, 20 rows (height of AA)
                for (int r = 0; r < 20; r++) {
                    // Column 1: AA 1-20
                    String seatIdAA = "AA" + (r + 1);
                    buttonsArray[r][0] = createStyledSeatButton(seatIdAA, S_WIDTH, S_HEIGHT);
                    buttonsArray[r][0].setText(String.valueOf(r + 1));
                    // Column 2: BB 1-7 (rows 0-6)
                    if (r < 7) {
                        String seatIdBB = "BB" + (r + 1);
                        buttonsArray[r][1] = createStyledSeatButton(seatIdBB, S_WIDTH, S_HEIGHT);
                        buttonsArray[r][1].setText(String.valueOf(r + 1));
                    }
                }
                row = seatNum - 1; // AA: 1-20 (rows 0-19)
                col = 0; // Column 1
            } else if (seatId.startsWith("BB") && seatNum >= 1 && seatNum <= 5) { // BB 1-5 (BL)
                buttonsArray = new JButton[20][2]; // Two columns, 20 rows (height of AA)
                for (int r = 0; r < 20; r++) {
                    // Column 1: AA 1-20
                    String seatIdAA = "AA" + (r + 1);
                    buttonsArray[r][0] = createStyledSeatButton(seatIdAA, S_WIDTH, S_HEIGHT);
                    buttonsArray[r][0].setText(String.valueOf(r + 1));
                    // Column 2: BB 1-5 (rows 0-4)
                    if (r < 5) {
                        String seatIdBB = "BB" + (r + 1);
                        buttonsArray[r][1] = createStyledSeatButton(seatIdBB, S_WIDTH, S_HEIGHT);
                        buttonsArray[r][1].setText(String.valueOf(r + 1));
                    }
                }
                row = seatNum - 1; // BB: 1-5 (rows 0-4)
                col = 1; // Column 2
            } else if (seatId.startsWith("BB") && seatNum >= 1 && seatNum <= 7) { // BB 1-7 (BR)
                buttonsArray = new JButton[20][2]; // Two columns, 20 rows (height of AA)
                for (int r = 0; r < 20; r++) {
                    // Column 1: AA 1-20
                    String seatIdAA = "AA" + (r + 1);
                    buttonsArray[r][0] = createStyledSeatButton(seatIdAA, S_WIDTH, S_HEIGHT);
                    buttonsArray[r][0].setText(String.valueOf(r + 1));
                    // Column 2: BB 1-7 (rows 0-6)
                    if (r < 7) {
                        String seatIdBB = "BB" + (r + 1);
                        buttonsArray[r][1] = createStyledSeatButton(seatIdBB, S_WIDTH, S_HEIGHT);
                        buttonsArray[r][1].setText(String.valueOf(r + 1));
                    }
                }
                row = seatNum - 1; // BB: 1-7 (rows 0-6)
                col = 1; // Column 2
            } else {
                return null; // Invalid seat ID
            }
        }

        if (row >= 0 && row < buttonsArray.length && col >= 0 && col < buttonsArray[row].length && buttonsArray[row][col] != null) {
            return buttonsArray[row][col];
        }
        return null;
    }


    private void refundSeat(String seatId, JButton seatButton) {
        // If it was a wheelchair seat, reset the adjacent gray seat
        SeatStatus currentStatus = seats.getOrDefault(seatId, SeatStatus.AVAILABLE);
        if (currentStatus != SeatStatus.AVAILABLE) {
            if (currentStatus == SeatStatus.WHEELCHAIR) {
                // Calculate row and column
                int seatNumber = Integer.parseInt(seatId.replaceAll("[^0-9]", "")) - 1;
                int row = seatNumber / BUTTONS_PER_ROW;
                int col = seatNumber % BUTTONS_PER_ROW;
                JButton[][] buttons = seatId.matches("[A-Q]\\d+") ? stallButtons : balconyButtons;

                // Check both left and right adjacent seats
                if (col > 0 && buttons[row][col-1].getBackground().equals(Color.GRAY)) {
                    resetAdjacentSeat(buttons[row][col-1], row, col-1, buttons);
                }
                if (col < BUTTONS_PER_ROW-1 && buttons[row][col+1].getBackground().equals(Color.GRAY)) {
                    resetAdjacentSeat(buttons[row][col+1], row, col+1, buttons);
                }
            }
            seats.put(seatId, SeatStatus.AVAILABLE);
            seatButton.setEnabled(true);
            seatButton.setText(seatId.replaceAll("[A-Z]+", "")); // Show only the seat number (e.g., "1")
            seatButton.setBackground(new Color(181, 222, 184));
            seatButton.setForeground(new Color(46, 83, 63));
            seatButton.setOpaque(true);
            seatButton.setBorder(new SimpleRoundedBorder(15, new Color(46, 83, 63)));

            JOptionPane.showMessageDialog(window, "Seat " + seatId + " has been refunded and is now available.");
            deleteSeatBooking(seatId);
        } else {
            JOptionPane.showMessageDialog(window, "Seat " + seatId + " is not booked.");
        }
    }

    private void resetAdjacentSeat(JButton button, int row, int col, JButton[][] buttons) {
        // Determine the section based on the buttons array
        String prefix = (buttons == stallButtons) ? "" : "";
        String adjacentSeatId = (prefix.equals("") ? rowLabelsStalls[row] : rowLabelsBalcony[row]) + (col + 1);
        button.setEnabled(true);
        button.setText(String.valueOf(col + 1)); // Show only the seat number
        button.setBackground(new Color(181, 222, 184));
        button.setOpaque(true);
        button.setBorder(new SimpleRoundedBorder(15, new Color(46, 83, 63)));
        seats.put(adjacentSeatId, SeatStatus.AVAILABLE);
    }

    // Add rowLabelsStalls and rowLabelsBalcony as class fields for use in resetAdjacentSeat
    private static final String[] rowLabelsStalls = {"Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"};
    private static final String[] rowLabelsBalcony = {"AA", "BB", "CC"};

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

    private void saveSeatBooking(String seatId, SeatStatus status, Integer customerId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO SeatStates (show_id, seat_number, status, room_id) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, showId);
            pstmt.setString(2, seatId); // Uses new seat ID format (e.g., "A1", "AA24")
            pstmt.setString(3, status.name());
            pstmt.setInt(4, roomId);
            pstmt.executeUpdate();

            if (status == SeatStatus.BOOKED || status == SeatStatus.RESERVED || status == SeatStatus.WHEELCHAIR) {
                try (PreparedStatement pstmt1 = conn.prepareStatement(
                        "INSERT INTO Tickets (booking_id, seat_number, price, customer_id, collection_status) VALUES (?, ?, ?, ?, ?)")) {
                    pstmt1.setInt(1, BookingId);
                    pstmt1.setString(2, seatId); // Uses new seat ID format
                    double price = 6.50; // Base price
                    double discount = 0.0;
                    if (customerId != null) {
                        Customer customer = CustomerManager.getCustomerById(customerId);
                        if (customer != null) {
                            if (customer.isFriend()) {
                                discount += 0.1; // 10% discount for Friends of Lancaster’s
                            }
                            if (customer.isNhs() || customer.isMilitary()) {
                                discount += 0.1; // Additional 10% discount for NHS or military
                            }
                        }
                    }
                    price *= (1 - discount); // Apply cumulative discount
                    pstmt1.setDouble(3, price);
                    pstmt1.setObject(4, customerId, java.sql.Types.INTEGER);
                    pstmt1.setString(5, "Not Collected");
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
            pstmt.setString(2, seatId); // Uses new seat ID format (e.g., "A1", "AA24")
            pstmt.setInt(3, roomId);
            pstmt.executeUpdate();

            try (PreparedStatement pstmt1 = conn.prepareStatement(
                    "DELETE FROM Tickets WHERE booking_id = ? AND seat_number = ?")) {
                pstmt1.setInt(1, BookingId);
                pstmt1.setString(2, seatId); // Uses new seat ID format
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
