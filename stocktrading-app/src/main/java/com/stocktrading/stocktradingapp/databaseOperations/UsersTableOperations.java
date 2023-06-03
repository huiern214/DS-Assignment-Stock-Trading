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
     * 
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

    public UserProfile getUserProfile(int userId) throws SQLException {
        StockTableOperations stockTable = new StockTableOperations(this.connection);
        String getUserProfileQuery = "SELECT u.user_id, u.username, u.email, u.funds, p.stock_id, p.quantity, p.purchase_price "
                +
                "FROM Users u " +
                "LEFT JOIN Portfolio p ON u.user_id = p.user_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(getUserProfileQuery)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int fetchedUserId = resultSet.getInt("user_id");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    double funds = resultSet.getDouble("funds");

                    UserProfile userProfile = new UserProfile(fetchedUserId, username, email);
                    userProfile.setFunds(connection, funds);

                    do {
                        String stockId = resultSet.getString("stock_id");
                        int quantity = resultSet.getInt("quantity");
                        double purchasePrice = resultSet.getDouble("purchase_price");

                        Stock stock = stockTable.getStock(stockId); // Assuming a method to retrieve stock details
                        PortfolioItem portfolioItem = new PortfolioItem(stock, quantity, purchasePrice);

                        userProfile.addPortfolioItem(portfolioItem);
                    } while (resultSet.next());

                    return userProfile;
                }
            }
        }

        return null; // User not found
    }

    // Additional methods for retrieving user information or performing other
    // operations on the Users table
}