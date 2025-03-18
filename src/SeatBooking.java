
import javax.swing.*;
import java.awt.*;

public class SeatBooking {
    public SeatBooking(JFrame window) {
        initialiseGui(window);
    }

    private void initialiseGui(JFrame window) {
        // Creates a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create the menu button
        JButton menuButton = createMenuButton();

        // Combine the menu button and screen button into a single panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        // Menu button aligned left
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.add(menuButton);
        topPanel.add(menuPanel, BorderLayout.WEST);

        



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
        return menuButton;
    }
    
}
