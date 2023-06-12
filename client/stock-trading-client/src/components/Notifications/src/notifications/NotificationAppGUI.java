/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package notifications;

/**
 *
 * @author Muhammad Abdullah Talukder S2191211
 */
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class NotificationAppGUI {
    private static final int NOTIFICATION_DELAY = 5000; // Delay in milliseconds (e.g., 5000 ms = 5 seconds)
    private static final String[] STOCK_NAMES = {"AAPL", "GOOG", "MSFT", "AMZN", "FB"}; // Sample stock names

    private JFrame frame;
    private JTextPane notificationPane;

    public NotificationAppGUI() {
        frame = new JFrame("Stock Notifications");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        notificationPane = new JTextPane();
        notificationPane.setEditable(false);
        notificationPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        frame.add(new JScrollPane(notificationPane), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public void displayNotification(String notification, Color color) {
        StyledDocument doc = notificationPane.getStyledDocument();
        Style style = notificationPane.addStyle("NotificationStyle", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), notification + "\n", style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NotificationAppGUI app = new NotificationAppGUI();
        Timer timer = new Timer();
        timer.schedule(app.new NotificationTask(), NOTIFICATION_DELAY, NOTIFICATION_DELAY);
    }

    class NotificationTask extends TimerTask {
        private Random random = new Random();

        @Override
        public void run() {
            int randomNumber = random.nextInt(5) + 1; // Generate a random number between 1 and 5
            boolean isProfit = random.nextBoolean(); // Randomly determine if it's a profit or loss
            String stockName = STOCK_NAMES[random.nextInt(STOCK_NAMES.length)]; // Randomly select a stock name

            String notification;
            Color color;
            if (isProfit) {
                notification = "[" + stockName + "] Your Profit is " + randomNumber + "%";
                color = Color.GREEN;
            } else {
                notification = "[" + stockName + "] Your Loss is " + randomNumber + "%";
                color = Color.RED;
            }
            displayNotification(notification, color);
        }
    }
}

