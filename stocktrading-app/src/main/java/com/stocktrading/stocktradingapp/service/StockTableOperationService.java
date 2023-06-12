package com.stocktrading.stocktradingapp.service;

import java.sql.*;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockTableOperationService {

    private final String databaseUrl = "jdbc:sqlite:stocktrading-app/src/main/java/com/stocktrading/stocktradingapp/database/data.sqlite3";
    private Connection connection;
    
    // establishes connection to the database
    public StockTableOperationService() throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
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

    public Stock getStock(String symbol) throws SQLException {
        String getStockQuery = "SELECT * FROM Stocks WHERE stock_symbol = ?";
        // String getStockQuery = "SELECT company_name, stock_symbol, current_price FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(getStockQuery)) {
            statement.setString(1, symbol);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String stockName = resultSet.getString("company_name");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    double currentPrice = resultSet.getDouble("current_price");

                    return new Stock(stockSymbol, stockName, currentPrice);
                }
            }
        }

        return null; // Stock not found
    }

    public void removeStock(String symbol) throws SQLException {
        String removeStockQuery = "DELETE FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setString(1, symbol);

            statement.executeUpdate();
        }
    }

    public void updateStockPrice(String symbol, double newPrice) throws SQLException {
        String updateStockQuery = "UPDATE Stocks SET current_price = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateStockQuery)) {
            statement.setDouble(1, newPrice);
            statement.setString(2, symbol);

            statement.executeUpdate();
        }
    }
}

