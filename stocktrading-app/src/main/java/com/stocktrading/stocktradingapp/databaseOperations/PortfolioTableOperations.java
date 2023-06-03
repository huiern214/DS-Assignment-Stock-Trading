package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.*;

public class PortfolioTableOperations {

    private Connection connection;

    public PortfolioTableOperations(Connection connection) {
        this.connection = connection;
    }

    public void addStockToPortfolio(int userId, String stockId, int quantity, double purchasePrice)
            throws SQLException {
        String addStockQuery = "INSERT INTO Portfolio (user_id, stock_id, quantity, purchase_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setInt(1, userId);
            statement.setString(2, stockId);
            statement.setInt(3, quantity);
            statement.setDouble(4, purchasePrice);

            statement.executeUpdate();
        }
    }

    public void sellStockFromPortfolio(int userId, String stockSymbol, int quantity, double sellPrice)
            throws SQLException {
        String getStockQuery = "SELECT portfolio_id, quantity, purchase_price FROM Portfolio WHERE user_id = ? AND stock_symbol = ? ORDER BY purchase_date";
        String updateStockQuery = "UPDATE Portfolio SET quantity = ?, purchase_price = ? WHERE portfolio_id = ?";
        String updateUserFundsQuery = "UPDATE Users SET funds = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(getStockQuery)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);

            try (ResultSet resultSet = statement.executeQuery()) {
                int remainingQuantity = quantity;

                while (resultSet.next() && remainingQuantity > 0) {
                    int portfolioId = resultSet.getInt("portfolio_id");
                    int currentQuantity = resultSet.getInt("quantity");
                    double purchasePrice = resultSet.getDouble("purchase_price");

                    int sellQuantity = Math.min(currentQuantity, remainingQuantity);

                    if (sellQuantity < currentQuantity) {
                        // Reduce the quantity of the current stock
                        int updatedQuantity = currentQuantity - sellQuantity;
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateStockQuery)) {
                            updateStatement.setInt(1, updatedQuantity);
                            updateStatement.setDouble(2, purchasePrice);
                            updateStatement.setInt(3, portfolioId);
                            updateStatement.executeUpdate();
                        }
                    } else {
                        // Remove the stock from the portfolio if selling all shares
                        removeStockFromPortfolio(portfolioId);
                    }

                    // Calculate the profit/loss for the sold quantity
                    double purchaseCost = purchasePrice * sellQuantity;
                    double sellCost = sellPrice * sellQuantity;
                    double profitLoss = sellCost - purchaseCost;

                    // Update user funds
                    double currentUserFunds = getUserFunds(userId);
                    double updatedUserFunds = currentUserFunds + profitLoss;

                    try (PreparedStatement updateFundsStatement = connection.prepareStatement(updateUserFundsQuery)) {
                        updateFundsStatement.setDouble(1, updatedUserFunds);
                        updateFundsStatement.setInt(2, userId);
                        updateFundsStatement.executeUpdate();
                    }

                    remainingQuantity -= sellQuantity;
                }
            }
        }

        // Perform additional logic for handling the sold stocks, such as updating
        // transaction history, etc.
    }

    private double getUserFunds(int userId) throws SQLException {
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

    private void removeStockFromPortfolio(int portfolioId) throws SQLException {
        String removeStockQuery = "DELETE FROM Portfolio WHERE portfolio_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setInt(1, portfolioId);
            statement.executeUpdate();
        }
    }

    public void removeStockFromPortfolio(int userId, String stockSymbol) throws SQLException {
        String removeStockQuery = "DELETE FROM Portfolio WHERE user_id = ? AND stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);

            statement.executeUpdate();
        }
    }

    // Other methods for portfolio-related operations...
}
