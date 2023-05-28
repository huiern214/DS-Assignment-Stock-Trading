import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAuthenticationGUI extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private JButton createAccountButton;

    private Map<String, String> userAccounts;

    public UserAuthenticationGUI() {
        // Set up the JFrame
        setTitle("User Authentication");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize user accounts map
        userAccounts = new HashMap<>();

        // Create the components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        showPasswordCheckBox = new JCheckBox("Show Password");
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create Account");

        // Set up the layout
        setLayout(new GridLayout(5, 2, 10, 10));
        getContentPane().setBackground(new Color(225, 225, 225)); // Set background color

        // Set label foreground color
        usernameLabel.setForeground(new Color(30, 30, 30));
        passwordLabel.setForeground(new Color(30, 30, 30));

        // Add components to the JFrame
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty label for spacing
        add(showPasswordCheckBox);
        add(new JLabel()); // Empty label for spacing
        add(loginButton);
        add(new JLabel()); // Empty label for spacing
        add(createAccountButton);

        // Set button colors
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        createAccountButton.setBackground(new Color(59, 89, 182));
        createAccountButton.setForeground(Color.WHITE);

        // Add action listeners to the buttons and checkbox
        loginButton.addActionListener(this);
        createAccountButton.addActionListener(this);
        showPasswordCheckBox.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Perform authentication check
            if (authenticateUser(username, password)) {
                showWelcomeMessage(username);
            } else {
                JOptionPane.showMessageDialog(this, "Authentication failed. Please try again.");
            }
        } else if (e.getSource() == createAccountButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Check if username is already taken
            if (userAccounts.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
            } else {
                String emailAddress = JOptionPane.showInputDialog(this, "Enter your email address:");
                if (isValidEmailAddress(emailAddress)) {
                    // Create the account
                    userAccounts.put(username, password);
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email address. Please try again.");
                }
            }
        } else if (e.getSource() == showPasswordCheckBox) {
            // Toggle password visibility
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? '\0' : '*');
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Check if the username exists and the password matches
        return userAccounts.containsKey(username) && userAccounts.get(username).equals(password);
    }

    private boolean isValidEmailAddress(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    private void showWelcomeMessage(String username) {
        // Create and show the welcome frame
        JFrame welcomeFrame = new JFrame();
        welcomeFrame.setTitle("Welcome");
        welcomeFrame.setSize(300, 200);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLocationRelativeTo(null);

        // Set the welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Add the label to the welcome frame
        welcomeFrame.add(welcomeLabel);

        // Set background color
        welcomeFrame.getContentPane().setBackground(new Color(225, 225, 225));

        // Display the welcome frame
        welcomeFrame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            UserAuthenticationGUI authenticationGUI = new UserAuthenticationGUI();
            authenticationGUI.setVisible(true);
        });
    }
}
