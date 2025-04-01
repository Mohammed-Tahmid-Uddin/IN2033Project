import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

public class CalendarGUI extends JPanel {
    private static final int DAYS_IN_WEEK = 7;
    private static final String[] DAY_NAMES = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private Calendar calendar;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private JButton prevButton, nextButton;

    public CalendarGUI() {
        calendar = Calendar.getInstance();
        setLayout(new BorderLayout());

        // Display the month label
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(monthLabel, BorderLayout.NORTH);

        // Navigation buttons for previous/next month
        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("<");
        nextButton = new JButton(">");
        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Panel to display the days of the month
        daysPanel = new JPanel(new GridLayout(0, DAYS_IN_WEEK));
        add(daysPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    private void updateCalendar() {
        // Update the month label
        monthLabel.setText(getMonthName(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR));

        // Clear previous days
        daysPanel.removeAll();

        // Add day names (Sun, Mon, Tue, etc.)
        for (String day : DAY_NAMES) {
            daysPanel.add(new JLabel(day, SwingConstants.CENTER));
        }

        // Get the first day of the month and the number of days in the month
        int firstDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_WEEK);  // First day of the week

        // Add empty labels to align the days properly
        for (int i = 1; i < startDay; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Add day numbers to the calendar
        for (int day = 1; day <= lastDayOfMonth; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            int finalDay = day;
            dayButton.addActionListener(e -> showDayEvents(finalDay));
            daysPanel.add(dayButton);
        }

        // Refresh the panel to display the updated calendar
        revalidate();
        repaint();
    }

    private void changeMonth(int offset) {
        calendar.add(Calendar.MONTH, offset);
        updateCalendar();
    }

    private String getMonthName(int monthIndex) {
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return monthNames[monthIndex];
    }

    private void showDayEvents(int day) {
        int month = calendar.get(Calendar.MONTH) + 1; // Month is 0-indexed
        int year = calendar.get(Calendar.YEAR);

        // Fetch events for the selected day
        ResultSet events = DatabaseConnection.getEventsForDate(year, month, day);

        // Display events (this could be expanded to show details in a new window)
        StringBuilder eventDetails = new StringBuilder("Events for " + day + "/" + month + "/" + year + ":\n");
        try {
            while (events.next()) {
                String eventName = events.getString("event_name"); // Assuming event name is stored in "event_name" column
                eventDetails.append(eventName).append("\n");
            }
            JOptionPane.showMessageDialog(this, eventDetails.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
