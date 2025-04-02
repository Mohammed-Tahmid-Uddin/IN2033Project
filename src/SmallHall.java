import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SmallHall {
    private Map<String, SeatStatus> seats;
    private JFrame window;
    private JButton[][] stallButtons;
    private Sidebar sidebar;

    // Constants
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTONS_PER_ROW = 20;

    // Seat status enum
    private enum SeatStatus {
        AVAILABLE, BOOKED, RESERVED, WHEELCHAIR
    }

    public SmallHall() {
        seats = new HashMap<>();
        initializeGui();
    }

    private void initializeGui() {
        window = new JFrame("Seat Booking System");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Initialize the sidebar
        sidebar = new Sidebar();
        mainPanel.add(sidebar.getSidebar(), BorderLayout.WEST);

        // Create the menu button
        JButton menuButton = createMenuButton();

        // Create the screen panel
        JPanel screenPanel = createScreenPanel();

        // Combine the menu button and screen panel into a single panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Add the menu button to the left of the top panel
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.add(menuButton);
        topPanel.add(menuPanel, BorderLayout.WEST);

        // Add the screen panel to the center of the top panel
        topPanel.add(screenPanel, BorderLayout.CENTER);

        // Add the top panel to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Create the stalls panel with 95 seats
        JPanel stallsPanel = createSeatPanel(95, "S ", new Color(240, 240, 240)); // Light gray background

        // Add the stalls panel to the main panel
        mainPanel.add(stallsPanel, BorderLayout.CENTER);

        // Add the main panel to the window
        window.add(mainPanel);
        window.setVisible(true);
    }

    // Method to create a seat panel
    private JPanel createSeatPanel(int numberOfSeats, String seatPrefix, Color backgroundColor) {
       // JPanel seatPanel = new JPanel(new GridLayout(0, BUTTONS_PER_ROW, 5, 5));
       JPanel seatPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE; // Prevents stretching 
       seatPanel.setBackground(backgroundColor);

        // Initialize the 2D array for seat buttons
        int rows = (int) Math.ceil((double) numberOfSeats / BUTTONS_PER_ROW);
        stallButtons = new JButton[rows][BUTTONS_PER_ROW];

        for (int i = 1; i <= numberOfSeats; i++) {
            String seatId = seatPrefix + i;
            JButton seatButton = new JButton(seatId);
            seatButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            seatButton.setEnabled(true);
            seatButton.setBackground(Color.WHITE);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            
            // Add action listener for seat booking/reserving/wheelchair options
            seatButton.addActionListener(e -> showSeatOptions(seatId, seatButton));

            // Calculate row and column for the seat
            int row = (i - 1) / BUTTONS_PER_ROW;
            int col = (i - 1) % BUTTONS_PER_ROW;
            stallButtons[row][col] = seatButton; // Store the button in the 2D array

            gbc.gridx = col;
            gbc.gridy = row;
            seatPanel.add(seatButton, gbc);

        }

        return seatPanel;
    }

    // Method to show seat options
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
                boolean adjacentSeatReserved = offerAdjacentSeatChoice(row, col);
                if (!adjacentSeatReserved) {
                    revertSeatStatus(seatId, seatButton);
                }
                break;
            default:
                break;
        }
    }

    // Method to update seat status
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
        } else {
            JOptionPane.showMessageDialog(window, seatId + " is already taken.");
        }
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

    // Method for adjacent seat removal
    private boolean offerAdjacentSeatChoice(int row, int col) {
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

        if (isValidAdjacentSeat(row, col, isLeft)) {
            int targetCol = isLeft ? col - 1 : col + 1;
            JButton adjacentButton = stallButtons[row][targetCol];
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
    private boolean isValidAdjacentSeat(int row, int col, boolean isLeft) {
        int targetCol = isLeft ? col - 1 : col + 1; // checks if we want to go left or right

        // Check if the target column is within bounds
        if (targetCol < 0 || targetCol >= BUTTONS_PER_ROW) {
            return false; // Out of bounds
        }

        // Check if the adjacent seat is available
        JButton adjacentButton = stallButtons[row][targetCol];
        return adjacentButton.isEnabled(); // Ensures it's not already booked or reserved
    }

    // Method to create the menu button
    private JButton createMenuButton() {
        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 24));
        menuButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> sidebar.toggleSidebar());
        return menuButton;
    }

    // Method to create the screen panel
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

    public static void main(String[] args) {
        new SmallHall();
    }
}