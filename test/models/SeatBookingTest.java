package models;

import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.Map;

public class SeatBookingTest {


    static class TestableSeatBooking extends SeatBooking {
        public TestableSeatBooking() {
            super(new JFrame());
        }

        @Override
        public int getLatestShowId() { return 1; }

        @Override
        public int getLatestRoomID() { return 1; }

        @Override
        public int getLatestBookingId() { return 1; }

        @Override
        public void initialiseGui(JFrame window) {}

        @Override
        public void loadSeat() {}

        @Override
        public void updateButton() {}

        public Map<String, SeatBooking.SeatStatus> getSeats() {
            return seats;
        }

    }

    @Test
    public void testIsValidAdjacentSeat_withEnabledSeatLeft() {
        JButton[][] buttons = new JButton[1][3];
        for (int i = 0; i < 3; i++) {
            buttons[0][i] = new JButton();
            buttons[0][i].setEnabled(true);
        }

        TestableSeatBooking booking = new TestableSeatBooking();
        boolean result = booking.isValidAdjacentSeat(0, 1, true, buttons);
        assertTrue(result);
    }

    @Test
    public void testIsValidAdjacentSeat_withDisabledSeatRight() {
        JButton[][] buttons = new JButton[1][3];
        for (int i = 0; i < 3; i++) {
            buttons[0][i] = new JButton();
            buttons[0][i].setEnabled(true);
        }
        buttons[0][2].setEnabled(false); // simulate booked seat

        TestableSeatBooking booking = new TestableSeatBooking();
        boolean result = booking.isValidAdjacentSeat(0, 1, false, buttons);
        assertFalse(result);
    }

    @Test
    public void testIsValidAdjacentSeat_outOfBoundsLeft() {
        JButton[][] buttons = new JButton[1][3];
        for (int i = 0; i < 3; i++) {
            buttons[0][i] = new JButton();
            buttons[0][i].setEnabled(true);
        }

        TestableSeatBooking booking = new TestableSeatBooking();
        boolean result = booking.isValidAdjacentSeat(0, 0, true, buttons); // left of col 0
        assertFalse(result);
    }

    @Test
    public void testIsValidAdjacentSeat_outOfBoundsRight() {
        JButton[][] buttons = new JButton[1][3];
        for (int i = 0; i < 3; i++) {
            buttons[0][i] = new JButton();
            buttons[0][i].setEnabled(true);
        }

        TestableSeatBooking booking = new TestableSeatBooking();
        boolean result = booking.isValidAdjacentSeat(0, 2, false, buttons); // right of col 2
        assertFalse(result);
    }

    @Test
    public void testResetAdjacentSeat_restoresButtonAndMap() {
        // Arrange
        TestableSeatBooking booking = new TestableSeatBooking();


        JButton button = new JButton();
        button.setEnabled(false);
        button.setText(""); // simulate cleared seat
        button.setBackground(Color.GRAY); // simulate reserved/disabled state

        int row = 0;
        int col = 2;
        String prefix = "S ";
        String expectedSeatId = "S 3"; // (row * BUTTONS_PER_ROW + col) + 1


        booking.resetAdjacentSeat(button, prefix, row, col);


        assertTrue(button.isEnabled(), "Button should be re-enabled");
        assertEquals(expectedSeatId, button.getText(), "Button text should show correct seat ID");
        assertEquals(Color.WHITE, button.getBackground(), "Button background should be reset to white");
        assertEquals("AVAILABLE", booking.getSeats().get(expectedSeatId).name(), "Seat status should be AVAILABLE");
    }

    @Test
    public void testRevertSeatStatus_restoresButtonAndMap() {

        TestableSeatBooking booking = new TestableSeatBooking();

        JButton button = new JButton();
        button.setEnabled(false);
        button.setText("S 3"); // simulate booked/reserved seat
        button.setBackground(Color.GRAY); // simulate reserved/disabled state

        String seatId = "S 3"; // simulate the seat ID being restored


        booking.revertSeatStatus(seatId, button);


        assertTrue(button.isEnabled(), "Button should be re-enabled");
        assertEquals(seatId, button.getText(), "Button text should show correct seat ID");
        assertEquals(Color.WHITE, button.getBackground(), "Button background should be reset to white");
        assertEquals("AVAILABLE", booking.getSeats().get(seatId).name(), "Seat status should be AVAILABLE");
    }


}
