import javax.swing.JFrame;
import java.awt.Color;


public class App {
    public static void main(String[] args) throws Exception {
        JFrame Window=new JFrame();
        DatabaseConnection.getConnection();


        Window.setVisible(true);
        Window.setExtendedState(JFrame.MAXIMIZED_BOTH);  
        Window.setTitle("Lancaster's Music Hall App");
        Window.getContentPane().setBackground(Color.WHITE);

        Window.setResizable(false);

       // SeatBooking seatBooking=new SeatBooking(Window);
        SmallerHall hall=new SmallerHall(Window);

    }
}