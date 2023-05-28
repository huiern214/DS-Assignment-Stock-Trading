/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.stocktrading.stocktradingapp.draft;
import java.util.Scanner;

/**
 *
 * @author abdullahiibrahim
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPanel extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminPanel() {
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (isValidAdmin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    openAdminOperationsWindow();
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

    private boolean isValidAdmin(String username, String password) {
        // Implement admin credentials validation logic
        String validUsername = "Abdullahi";
        String validPassword = "admin123@13";
        return username.equals(validUsername) && password.equals(validPassword);
    }

    private void openAdminOperationsWindow() {
        JFrame adminOperationsFrame = new JFrame("Admin Panel Operations");
        adminOperationsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminOperationsFrame.setSize(400, 300);
        adminOperationsFrame.setLocationRelativeTo(null);
        adminOperationsFrame.setLayout(new GridLayout(7, 1));

        JButton addParticipantButton = new JButton("Add Participant");
        JButton updateParticipantPointsButton = new JButton("Update Participant Points");
        JButton displayParticipantStatisticsButton = new JButton("Display Participant Statistics");
        JButton displayDisqualifiedParticipantsButton = new JButton("Display Disqualified Participants");
        JButton disqualifyParticipantButton = new JButton("Disqualify Participant");
        JButton monitorTransactionsButton = new JButton("Monitor Transactions");
        JButton exitButton = new JButton("Exit");

        addParticipantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle add participant button click
                JOptionPane.showMessageDialog(null, "Add Participant button clicked.");
            }
        });

        updateParticipantPointsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle update participant points button click
                JOptionPane.showMessageDialog(null, "Update Participant Points button clicked.");
            }
        });

        displayParticipantStatisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle display participant statistics button click
                JOptionPane.showMessageDialog(null, "Display Participant Statistics button clicked.");
            }
        });

        displayDisqualifiedParticipantsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle display disqualified participants button click
                JOptionPane.showMessageDialog(null, "Display Disqualified Participants button clicked.");
            }
        });

        disqualifyParticipantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle disqualify participant button click
                JOptionPane.showMessageDialog(null, "Disqualify Participant button clicked.");
            }
        });

        monitorTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle monitor transactions button click
                JOptionPane.showMessageDialog(null, "Monitor Transactions button clicked.");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle exit button click
                JOptionPane.showMessageDialog(null, "Exiting Admin Panel...");
                System.exit(0);
            }
        });

        adminOperationsFrame.add(addParticipantButton);
        adminOperationsFrame.add(updateParticipantPointsButton);
        adminOperationsFrame.add(displayParticipantStatisticsButton);
        adminOperationsFrame.add(displayDisqualifiedParticipantsButton);
        adminOperationsFrame.add(disqualifyParticipantButton);
        adminOperationsFrame.add(monitorTransactionsButton);
        adminOperationsFrame.add(exitButton);

        adminOperationsFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminPanel();
            }
        });
    }
}



