/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package balaamr;

/**
 *
 * @author Muhammad Abdullah Talukder S2191211
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LeaderboardSystem {
    private static final int MIN_RANDOM_NUMBER = -1000; // Changed to negative values for subtraction
    private static final int MAX_RANDOM_NUMBER = 1000;
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds

    public static void main(String[] args) {
        List<User> leaderboard = createLeaderboard();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTask(leaderboard), UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    private static List<User> createLeaderboard() {
        List<User> leaderboard = new ArrayList<>();
        String[] names = {
                "Ahmad",
                "Hafiz",
                "Nurul",
                "Siti",
                "Kumar",
                "Tan",
                "Wong",
                "Lee",
                "Abdullah",
                "Lim"
        };
        String[] lastNames = {
                "Ibrahim",
                "Rahman",
                "Ali",
                "Mohammed",
                "Khan",
                "Chin",
                "Tan",
                "Lee",
                "Abdul",
                "Lim"
        };

        for (int i = 0; i < names.length; i++) {
            User user = new User(names[i], lastNames[i], 50000);
            leaderboard.add(user);
        }

        return leaderboard;
    }

    private static class UpdateTask extends TimerTask {
        private List<User> leaderboard;
        private Random random;

        public UpdateTask(List<User> leaderboard) {
            this.leaderboard = leaderboard;
            this.random = new Random();
        }

        @Override
        public void run() {
            for (User user : leaderboard) {
                int randomNumber = generateRandomNumber();
                int updatedCredits = user.getCredits() + randomNumber;
                boolean isAdded = randomNumber >= 0;
                String colorCode = isAdded ? "\u001B[32m" : "\u001B[31m";
                String amountText = isAdded ? "(+" + Math.abs(randomNumber) + ")" : "(-" + Math.abs(randomNumber) + ")";

                user.setCredits(updatedCredits);
                user.updateMovement(leaderboard);

                System.out.printf("%s %s%s%s - Credits: %d %s%s%s%n",
                        user.getFullName(),
                        colorCode,
                        amountText,
                        "\u001B[0m",
                        user.getCredits(),
                        colorCode,
                        user.getMovement(),
                        "\u001B[0m");
            }

            leaderboard.sort(Comparator.comparingInt(User::getCredits).reversed());
            System.out.println("--------------------");
        }

        private int generateRandomNumber() {
            return random.nextInt(MAX_RANDOM_NUMBER - MIN_RANDOM_NUMBER + 1) + MIN_RANDOM_NUMBER;
        }
    }
}

class User {
    private String firstName;
    private String lastName;
    private int credits;
    private int previousPosition;

    public User(String firstName, String lastName, int credits) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.credits = credits;
        this.previousPosition = -1;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void updateMovement(List<User> leaderboard) {
        int currentPosition = leaderboard.indexOf(this);
        if (previousPosition == -1) {
            previousPosition = currentPosition;
        } else if (currentPosition < previousPosition) {
            previousPosition = currentPosition;
        } else if (currentPosition > previousPosition) {
            previousPosition = currentPosition;
        }
    }

    public String getMovement() {
        int currentPosition = previousPosition + 1;
        if (currentPosition < 1) {
            return "";
        }
        return currentPosition < 10 ? "(moved up from 0" + previousPosition + " to 0" + currentPosition + ")"
                : "(moved up from " + previousPosition + " to " + currentPosition + ")";
    }
}
