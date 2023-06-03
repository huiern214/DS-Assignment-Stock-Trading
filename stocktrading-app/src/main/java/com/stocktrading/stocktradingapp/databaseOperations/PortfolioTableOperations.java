package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.*;

public class PortfolioTableOperations {

    private Connection connection;

    public PortfolioTableOperations(Connection connection) {
        this.connection = connection;
    }

    public void addStockToPortfolio(int userId, int stockId, int quantity, double purchasePrice) throws SQLException {
        String addStockQuery = "INSERT INTO Portfolio (user_id, stock_id, quantity, purchase_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setInt(1, userId);
            statement.setInt(2, stockId);
            statement.setInt(3, quantity);
            statement.setDouble(4, purchasePrice);

            statement.executeUpdate();
        }
    }

    public void removeStockFromPortfolio(int userId, int stockId) throws SQLException {
        String removeStockQuery = "DELETE FROM Portfolio WHERE user_id = ? AND stock_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setInt(1, userId);
            statement.setInt(2, stockId);

            statement.executeUpdate();
        }
    }

    // Other methods for portfolio-related operations...
}
