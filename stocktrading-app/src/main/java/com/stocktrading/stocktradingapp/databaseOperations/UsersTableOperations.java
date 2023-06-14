package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersTableOperations {

    private Connection connection;

    // establishes connection to the database
    public UsersTableOperations(Connection connection) {
        this.connection = connection;
    }

    // add user to the email by their username, email and password
    public boolean addUser(String username, String email, String password) throws SQLException {
        String addUserQuery = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(addUserQuery)) {
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
        String deleteUserQuery = "DELETE FROM Users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(deleteUserQuery)) {
            statement.setInt(1, userId);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    public void updateUserEmail(int userId, String newEmail) throws SQLException {
        String updateUserEmailQuery = "UPDATE Users SET email = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateUserEmailQuery)) {
            statement.setString(1, newEmail);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public void updateUserPassword(int userId, String newPassword) throws SQLException {
        String updateUserPasswordQuery = "UPDATE Users SET password = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateUserPasswordQuery)) {
            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public String getUserEmail(int userId) throws SQLException {
        String getUserEmailQuery = "SELECT email FROM Users WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(getUserEmailQuery)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        }

        return null; // Return null if user is not found
    }

    /**
     * Used to add an entry to Portfolio with the user's id,
     * @param userId
     * @throws SQLException
     */
    private void addPortfolio(int userId) throws SQLException {
        String addPortfolioQuery = "INSERT INTO Portfolio (user_id) VALUES (?)";

        try (PreparedStatement statement = connection.prepareStatement(addPortfolioQuery)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public double getUserFunds(int userId) throws SQLException {
        String getUserFundsQuery = "SELECT funds FROM Users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(getUserFundsQuery)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("funds");
                }
            }
        }
        throw new SQLException("Failed to retrieve user funds");
    }

    public void updateUserFunds(int userId,double newFunds) throws SQLException {
        String updateUserFundsQuery = "UPDATE Users SET funds = ? WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateUserFundsQuery)) {
            statement.setDouble(1, newFunds);
            statement.setInt(2, userId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("Failed to set user funds");
        }


    }

    // Additional methods for retrieving user information or performing other
    // operations on the Users table
}