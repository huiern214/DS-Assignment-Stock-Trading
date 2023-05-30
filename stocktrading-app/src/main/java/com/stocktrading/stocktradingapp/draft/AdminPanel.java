/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

/**
 *
 * @author Muhammad Abdullah Talukder S2191211
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AdminPanel extends JFrame {
    private JTextArea userListTextArea;
    private JButton removeButton;
    private JButton eraseButton;

    public AdminPanel() {
        setTitle("Admin Panel");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userListTextArea = new JTextArea();
        userListTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(userListTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        removeButton = new JButton("Remove User");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(AdminPanel.this, "Enter the username to remove:");
                if (username != null && !username.isEmpty()) {
                    removeUser(username);
                }
            }
        });
        buttonPanel.add(removeButton);

        eraseButton = new JButton("Erase User Accounts");
        eraseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(AdminPanel.this,
                        "Are you sure you want to erase all user accounts?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    eraseUserAccounts();
                }
            }
        });
        buttonPanel.add(eraseButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Display user accounts
        showUserAccounts();
    }

    private void showUserAccounts() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("userAccounts.txt"));
            String line;
            StringBuilder userList = new StringBuilder();
            int count = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String username = parts[0];
                    userList.append(count).append(". Username: ").append(username).append("\n");
                    count++;
                }
            }
            reader.close();
            userListTextArea.setText(userList.toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while reading user accounts.");
        }
    }

    private void removeUser(String username) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("userAccounts.txt"));
            StringBuilder updatedContent = new StringBuilder();
            String line;
            boolean removed = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String existingUsername = parts[0];
                    if (!existingUsername.equals(username)) {
                        updatedContent.append(line).append("\n");
                    } else {
                        removed = true;
                    }
                }
            }
            reader.close();

            if (removed) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("userAccounts.txt"));
                writer.write(updatedContent.toString());
                writer.close();
                JOptionPane.showMessageDialog(this, "User account has been removed successfully!");
                // Refresh user accounts display
                showUserAccounts();
            } else {
                JOptionPane.showMessageDialog(this, "User account not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while removing user account.");
        }
    }

    private void eraseUserAccounts() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("userAccounts.txt"));
            writer.write("");
            writer.close();
            JOptionPane.showMessageDialog(this, "All user accounts have been erased successfully!");
            // Refresh user accounts display
            showUserAccounts();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while erasing user accounts.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPanel::new);
    }
}










