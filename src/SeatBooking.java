
import javax.swing.*;
import java.awt.*;

public class SeatBooking {
    private Sidebar sidebar; 

    private static final int BUTTON_WIDTH = 30; 
    private static final int BUTTON_HEIGHT = 30;
    private static final int BUTTONS_PER_ROW = 20;

    public SeatBooking(JFrame window) {
        initialiseGui(window);
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
        JPanel stallsPanel = createSeatPanel(285, "S ", new Color(240, 240, 240)); // Light gray background
        JPanel balconyPanel = createSeatPanel(89, "B ", new Color(240, 240, 240)); // Light gray background

        // Combine stalls and balcony panels into a single panel
        JPanel seatPanels = new JPanel(new GridLayout(2, 1, 10, 10));
        seatPanels.add(stallsPanel);
        seatPanels.add(balconyPanel);

        // Add the seat panels to the main panel
         mainPanel.add(seatPanels, BorderLayout.CENTER);



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
    
        for (int i = 1; i <= numberOfSeats; i++) {
            String seatId = seatPrefix + i;
            JButton seatButton = new JButton(seatId);
            seatButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            seatButton.setEnabled(true);
            seatButton.setBackground(Color.WHITE);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            seatPanel.add(seatButton);
        }
    
        return seatPanel;
    }
    
}
