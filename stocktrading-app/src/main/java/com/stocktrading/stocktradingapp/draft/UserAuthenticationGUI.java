/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.stocktrading.stocktradingapp.draft;

/**
 *
 * @author Muhammad Abdullah Talukder S2191211
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UserAuthenticationGUI extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private JButton createAccountButton;

    private Map<String, String> userAccounts;
    private File userAccountsFile;

    public UserAuthenticationGUI() {
        // Set up the JFrame
        setTitle("User Authentication");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize user accounts map
        userAccounts = new HashMap<>();
        userAccountsFile = new File("userAccounts.txt");

        // Load user accounts from the file
        loadUserAccounts();

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
            String emailAddress = JOptionPane.showInputDialog(this, "Enter your email address:");
            if (isValidEmailAddress(emailAddress)) {
                String username = JOptionPane.showInputDialog(this, "Enter a username:");
                String password = JOptionPane.showInputDialog(this, "Enter a password:");

                // Check if username is already taken
                if (userAccounts.containsKey(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.");
                } else {
                    // Create the account
                    userAccounts.put(username, password);
                    storeUserAccount(username, password);
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email address. Please try again.");
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

    private void saveUserAccounts() {
        try (PrintWriter writer = new PrintWriter(userAccountsFile)) {
            for (Map.Entry<String, String> entry : userAccounts.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserAccounts() {
        if (userAccountsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userAccountsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        userAccounts.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void storeUserAccount(String username, String password) {
    try {
        BufferedWriter writer = new BufferedWriter(new FileWriter("userAccounts.txt", true));
        writer.write(username + "," + password);
        writer.newLine();
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error occurred while creating the user account.");
    }
}

    public static void main(String[] args) {
        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            UserAuthenticationGUI authenticationGUI = new UserAuthenticationGUI();
            authenticationGUI.setVisible(true);
        });
    }
}

