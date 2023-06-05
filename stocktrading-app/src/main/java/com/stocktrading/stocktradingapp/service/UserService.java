package com.stocktrading.stocktradingapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.UserProfile;

@Service
public class UserService {
    private final String databaseUrl = "jdbc:sqlite:stocktrading-app/src/main/java/com/stocktrading/stocktradingapp/database/data.sqlite3";

    // add user to the email by their username, email and password
    public boolean addUser(String username, String email, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (username, email, password) VALUES (?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, password);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    // deletes a user from the database
    public boolean deleteUser(int userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
            statement.setInt(1, userId);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    public void updateUserEmail(int userId, String newEmail) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("UPDATE Users SET email = ? WHERE user_id = ?")) {
            statement.setString(1, newEmail);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public void updateUserPassword(int userId, String newPassword) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("UPDATE Users SET password = ? WHERE user_id = ?")) {
            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public String getUserEmail(int userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("SELECT email FROM Users WHERE user_id = ?")) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        }

        return null; // Return null if user is not found
    }

    public boolean authenticateUser(String email, String password) {
        // Perform authentication logic, e.g., validate email and password against the database
        String query = "SELECT COUNT(*) FROM Users WHERE email = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(databaseUrl);
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return false;
    }

    public UserProfile getUserProfile(String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement statement = connection.prepareStatement("SELECT username, email, funds FROM Users WHERE email = ?")) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String userEmail = resultSet.getString("email");
                    Double funds = resultSet.getDouble("funds");

                    // Create a UserProfile object and return it
                    return new UserProfile(username, userEmail, funds);
                }
            }
        }

        return null; // User not found
    }
}
