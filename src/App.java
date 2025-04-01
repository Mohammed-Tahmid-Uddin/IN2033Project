import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread to ensure proper updates
        SwingUtilities.invokeLater(() -> {
            new App().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        // Create a JFrame (main application window)
        JFrame window = new JFrame("Lancaster's Music Hall App");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ensure the app exits when the window is closed
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Make the window fullscreen
        window.getContentPane().setBackground(Color.WHITE);  // Set the background color of the window
        window.setResizable(false);  // Make the window non-resizable

        // Create an instance of CalendarGUI and add it to the JFrame
        CalendarGUI calendarPanel = new CalendarGUI();
        window.add(calendarPanel);  // Add the CalendarGUI panel to the window

        // Make the window visible
        window.setSize(600, 600);  // Set the size of the window (optional, since it's maximized)
        window.setLocationRelativeTo(null);  // Center the window on the screen
        window.setVisible(true);  // Display the window
    }
}
