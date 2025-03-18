import javax.swing.*;
import java.awt.*;

public class Sidebar {
    private JPanel sidebar;
    private boolean sidebarVisible = false;

    public Sidebar() {
        
        initialiseSidebar();
    }

    private void initialiseSidebar() {
        sidebar = new JPanel();
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setPreferredSize(new Dimension(200, 600)); 
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Add buttons to the sidebar
        addSidebarButton("Home");
        addSidebarButton("Create Report");
       

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
    }

    public JPanel getSidebar() {
        return sidebar;
    }

    public void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
    }
}