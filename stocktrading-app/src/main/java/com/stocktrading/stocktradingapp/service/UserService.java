package com.stocktrading.stocktradingapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.model.User;
import com.stocktrading.stocktradingapp.model.Portfolio;

@Service
public class UserService {
    private final String databaseUrl = "jdbc:sqlite:stocktrading-app/database/data.sqlite3";
    private Connection connection;
    
    // establishes connection to the database
    public UserService() throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
    }

    // registers a user to the database
    // add user to the email by their username, email and password
    public boolean addUser(String username, String email, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (username, email, password) VALUES (?, ?, ?)")) {
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
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
            statement.setInt(1, userId);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return false;
    }

    public void updateUserEmail(int userId, String newEmail) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Users SET email = ? WHERE user_id = ?")) {
            statement.setString(1, newEmail);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public void updateUserPassword(int userId, String newPassword) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Users SET password = ? WHERE user_id = ?")) {
            statement.setString(1, newPassword);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }
    }

    public String getUserEmail(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT email FROM Users WHERE user_id = ?")) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        }

        return null; // Return null if user is not found
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

    // Add validation for duplicate users
    public boolean checkDuplicateEmail(String email) throws SQLException {
        String checkDuplicateEmailQuery = "SELECT email FROM Users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkDuplicateEmailQuery)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Authenticate user by email and password
    public int authenticateUser(String email, String password) {
        String query = "SELECT user_id FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // User not found or incorrect password
    }    

    // Get user profile by user id
    public User getUser(int userId) throws SQLException {
        String getUserProfileQuery = "SELECT u.user_id, u.username, u.email, u.password, u.funds, p.stock_symbol, p.quantity, p.purchase_price " +
                "FROM Users u " +
                "LEFT JOIN Portfolio p ON u.user_id = p.user_id " +
                "WHERE u.user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(getUserProfileQuery)) {
            StockTableOperationService stockTable = new StockTableOperationService();

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int fetchedUserId = resultSet.getInt("user_id");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    double funds = resultSet.getDouble("funds");

                    System.out.println("Stock Symbol: " + resultSet.getString("stock_symbol"));
                    System.out.println("Quantity: " + resultSet.getInt("quantity"));
                    System.out.println("Purchase Price: " + resultSet.getDouble("purchase_price"));

                    // Create a User object 
                    User user = new User(username, email, password);
                    user.setUserId(fetchedUserId);
                    user.setFunds(funds);

                    do {
                        String symbol = resultSet.getString("stock_symbol");
                        int quantity = resultSet.getInt("quantity");
                        double purchasePrice = resultSet.getDouble("purchase_price");          
                        Stock stock = stockTable.getStock(symbol); // Assuming a method to retrieve stock details
                        // System.out.println("Stock: " + stock.getName() + " " + stock.getSymbol() + " " + stock.getPrice());
                        Portfolio portfolio = new Portfolio();
                        portfolio.addStock(stock, quantity);
                        user.setPortfolio(portfolio);
                    } while (resultSet.next());

                    return user;
                }
            }
        }

        return null; // User not found
    }
}
