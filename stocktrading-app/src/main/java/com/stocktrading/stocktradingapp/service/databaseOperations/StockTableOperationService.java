package com.stocktrading.stocktradingapp.service.databaseOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockTableOperationService {

    private Connection connection;
    
    // establishes connection to the database
    public StockTableOperationService(Connection connection) {
        this.connection = connection;
    }

    // add stock to the database
    public void addStock(String stockName, String stockSymbol, double currentPrice) throws SQLException {
        String addStockQuery = "INSERT INTO Stocks (company_name, stock_symbol, current_price) VALUES (?, ?, ?)";

        // check if stock already exists
        if (getStock(stockSymbol) != null) {
            updateStockPrice(stockSymbol, currentPrice);
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setString(1, stockName);
            statement.setString(2, stockSymbol);
            statement.setDouble(3, currentPrice);

            statement.executeUpdate();
        }
    }

    public void addStock(String stockName, String stockSymbol, double currentPrice, double priceChange, double priceChangePercent) throws SQLException {
        String addStockQuery = "INSERT INTO Stocks (company_name, stock_symbol, current_price, price_change, price_change_percent) VALUES (?, ?, ?, ?, ?)";

        // check if stock already exists
        if (getStock(stockSymbol) != null) {
            updateStockPrice(stockSymbol, currentPrice);
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setString(1, stockName);
            statement.setString(2, stockSymbol);
            statement.setDouble(3, currentPrice);
            statement.setDouble(4, priceChange);
            statement.setDouble(5, priceChangePercent);

            statement.executeUpdate();
        }
    }

    // get stock from the database
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
                    double priceChange = resultSet.getDouble("price_change");
                    double priceChangePercent = resultSet.getDouble("price_change_percent");
                    int quantity = resultSet.getInt("quantity");

                    return new Stock(stockSymbol, stockName, currentPrice, priceChange, priceChangePercent, quantity);
                }
            }
        }

        return null; // Stock not found
    }

    // get all stocks from the database
    public List<Stock> getAllStocks() throws SQLException {
        String getAllStocksQuery = "SELECT * FROM Stocks";

        try (PreparedStatement statement = connection.prepareStatement(getAllStocksQuery)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Stock> stocks = new ArrayList<>();

                while (resultSet.next()) {
                    String stockName = resultSet.getString("company_name");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    double currentPrice = resultSet.getDouble("current_price");
                    double priceChange = resultSet.getDouble("price_change");
                    double priceChangePercent = resultSet.getDouble("price_change_percent");
                    int quantity = resultSet.getInt("quantity");

                    stocks.add(new Stock(stockSymbol, stockName, currentPrice, priceChange, priceChangePercent, quantity));
                }

                return stocks;
            }
        }
    }

    // remove stock from the database
    public void removeStock(String symbol) throws SQLException {
        String removeStockQuery = "DELETE FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setString(1, symbol);

            statement.executeUpdate();
        }
    }

    // update stock price in the database
    public void updateStockPrice(String symbol, double newPrice) throws SQLException {
        String updateStockQuery = "UPDATE Stocks SET current_price = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateStockQuery)) {
            statement.setDouble(1, newPrice);
            statement.setString(2, symbol);
            statement.executeUpdate();
        }
    }

    // update stock price change in the database
    public void updateStockPriceChange(String symbol, double newPriceChange) throws SQLException {
        String updateStockQuery = "UPDATE Stocks SET price_change = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateStockQuery)) {
            statement.setDouble(1, newPriceChange);
            statement.setString(2, symbol);
            statement.executeUpdate();
        }
    }

    // update stock price change percent in the database
    public void updateStockPriceChangePercent(String symbol, double newPriceChangePercent) throws SQLException {
        String updateStockQuery = "UPDATE Stocks SET price_change_percent = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateStockQuery)) {
            statement.setDouble(1, newPriceChangePercent);
            statement.setString(2, symbol);
            statement.executeUpdate();
        }
    }

    // get stock quantity from the database
    public int getStockQuantity(String symbol) {
        String query = "SELECT quantity FROM Stocks WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, symbol);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        return 0; // Return 0 if the stock symbol is not found or an error occurs
    }

    // update stock quantity in the database
    public void updateStockQuantity(String symbol, int quantity) {
        String query = "UPDATE Stocks SET quantity = ? WHERE stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setString(2, symbol);

            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }
    }

    // Update all stock quantities to 500 lot in the database
    public void updateAllStocksQuantity(int quantity) {
        String query = "UPDATE Stocks SET quantity = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }
    }
}

