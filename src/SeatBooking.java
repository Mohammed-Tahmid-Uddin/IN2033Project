
import javax.swing.*;
import java.awt.*;

public class SeatBooking {
    public SeatBooking(JFrame window) {
        initialiseGui(window);
    }

    private void initialiseGui(JFrame window) {
        // Creates a main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Adds the main panel to the window
        window.add(mainPanel);
        window.revalidate(); 
    }
}
