package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.*;

public class StockTableOperations {
    private Connection connection;

    // establishes connection to the database
    public StockTableOperations(Connection connection) {
        this.connection = connection;
    }

    public void addStock(String stockName, String stockSymbol, double currentPrice) throws SQLException {
        String addStockQuery = "INSERT INTO Stocks (company_name, stock_symbol, current_price) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setString(1, stockName);
            statement.setString(2, stockSymbol);
            statement.setDouble(3, currentPrice);

            statement.executeUpdate();
        }
    }

    public Stock getStock(String stockId) throws SQLException {
        String getStockQuery = "SELECT company_name, stock_symbol, current_price  FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(getStockQuery)) {
            statement.setString(1, stockId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String stockName = resultSet.getString("company_name");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    double currentPrice = resultSet.getDouble("current_price");

                    return new Stock(stockName, stockSymbol, currentPrice);
                }
            }
        }

        return null; // Stock not found
    }

    public void removeStock(String stockId) throws SQLException {
        String removeStockQuery = "DELETE FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setString(1, stockId);

            statement.executeUpdate();
        }
    }

    public void updateStockPrice(String stockId, double newPrice) throws SQLException {
        String updateStockQuery = "UPDATE Stocks SET current_price = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateStockQuery)) {
            statement.setDouble(1, newPrice);
            statement.setString(2, stockId);

            statement.executeUpdate();
        }
    }
}
