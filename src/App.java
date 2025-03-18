import javax.swing.JFrame;
import java.awt.Color;


public class App {
    public static void main(String[] args) throws Exception {
        JFrame Window=new JFrame();

        Window.setVisible(true);
        Window.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        Window.setTitle("Lancaster's Music Hall App");
        Window.getContentPane().setBackground(Color.WHITE);

        Window.setResizable(false);

        SeatBooking seatBooking=new SeatBooking(Window);

    }
}