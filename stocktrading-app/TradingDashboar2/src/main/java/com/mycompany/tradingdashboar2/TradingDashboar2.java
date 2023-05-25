/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.tradingdashboar2;
import java.util.Scanner;
/**
 *
 * @author abdullahiibrahim
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TradingDashboar2 extends JFrame {

    public TradingDashboar2() {
        setTitle("Trading Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (isValidParticipant(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    openTradingOperationsWindow();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Access denied.");
                }
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty label for spacing
        add(loginButton);

        setVisible(true);
    }

    private boolean isValidParticipant(String username, String password) {
        // Implement participant credentials validation logic
        String validUsername = "participant";
        String validPassword = "participant123";
        return username.equals(validUsername) && password.equals(validPassword);
    }

    private void openTradingOperationsWindow() {
        JFrame tradingOperationsFrame = new JFrame("Trading Dashboard Operations");
        tradingOperationsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tradingOperationsFrame.setSize(400, 300);
        tradingOperationsFrame.setLocationRelativeTo(null);
        tradingOperationsFrame.setLayout(new GridLayout(6, 1));

        JButton viewAccountBalanceButton = new JButton("View Account Balance");
        JButton viewPortfolioButton = new JButton("View Portfolio");
        JButton viewCurrentpointsButton = new JButton("Current points");
        JButton viewTradehistoryButton= new JButton("Trade history");
        JButton viewOpenpositionsButton = new JButton("-0987654321open positions");
        JButton exitButton = new JButton("Exit");

        viewAccountBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle view account balance button click
                JOptionPane.showMessageDialog(null, "Account balance: $1000");
            }
        });

        viewPortfolioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle view portfolio button click
                JOptionPane.showMessageDialog(null, "Viewing participant's portfolio...");
            }
        });
        viewCurrentpointsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle view portfolio button click
                JOptionPane.showMessageDialog(null, "Viewing participant's portfolio...");
            }
        });
         viewTradehistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle view portfolio button click
                JOptionPane.showMessageDialog(null, "Viewing participant's portfolio...");
            }
        });
        viewOpenpositionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle view portfolio button click
                JOptionPane.showMessageDialog(null, "Viewing participant's portfolio...");
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle exit button click
                JOptionPane.showMessageDialog(null, "Exiting Trading Dashboard...");
                System.exit(0);
            }
        });

        tradingOperationsFrame.add(viewAccountBalanceButton);
        tradingOperationsFrame.add(viewPortfolioButton);
        tradingOperationsFrame.add(viewCurrentpointsButton);
        tradingOperationsFrame.add(viewTradehistoryButton);
        tradingOperationsFrame.add(viewOpenpositionsButton);
        tradingOperationsFrame.add(exitButton);

        tradingOperationsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TradingDashboar2();
            }
        });
    }
}


    
