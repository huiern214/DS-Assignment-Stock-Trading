package com.stocktrading.stocktradingapp.databaseOperations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortfolioTableOperations {

    private Connection connection;

    public PortfolioTableOperations(Connection connection) {this.connection = connection;}

    public void addStockToPortfolio(int userId, String stockSymbol, int quantity, double purchasePrice) throws SQLException {
        String addStockQuery = "INSERT INTO Portfolio (user_id, stock_symbol, quantity, purchase_price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(addStockQuery)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);
            statement.setInt(3, quantity);
            statement.setDouble(4, purchasePrice);

            statement.executeUpdate();
        }
    }

    public void removeStockFromPortfolioByPortfolioId(int portfolioId) throws SQLException {
        String removeStockQuery = "DELETE FROM Portfolio WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(removeStockQuery)) {
            statement.setInt(1, portfolioId);
            statement.executeUpdate();
        }
    }

    public void addOrUpdateStock(int userId, String stockSymbol, int quantity, double price) throws SQLException {
        String query = "SELECT * FROM Portfolio WHERE user_id = ? AND stock_symbol = ?";
        boolean stockExists = false;

        try (PreparedStatement selectStatement = connection.prepareStatement(query)) {
            selectStatement.setInt(1, userId);
            selectStatement.setString(2, stockSymbol);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    stockExists = true;
                }
            }
        }

        if (stockExists) {
            // Update existing stock in portfolio
            String updateQuery = "UPDATE Portfolio SET quantity = ?, purchase_price = ? WHERE user_id = ? AND stock_symbol = ?";

            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, quantity);
                updateStatement.setDouble(2, price);
                updateStatement.setInt(3, userId);
                updateStatement.setString(4, stockSymbol);

                updateStatement.executeUpdate();
            }
        } else {
            // Add new stock to portfolio
            String insertQuery = "INSERT INTO Portfolio (user_id, stock_symbol, quantity, purchase_price) VALUES (?, ?, ?, ?)";

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, userId);
                insertStatement.setString(2, stockSymbol);
                insertStatement.setInt(3, quantity);
                insertStatement.setDouble(4, price);

                insertStatement.executeUpdate();
            }
        }
    }

    public void updateStockQuantity(int portfolioId, String stockSymbol, int quantity) throws SQLException {
        String updateQuery = "UPDATE Portfolio SET quantity = ? WHERE id = ? AND stock_symbol = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, quantity);
            statement.setInt(2, portfolioId);
            statement.setString(3, stockSymbol);

            statement.executeUpdate();
        }
    }

    public int getPortfolioQuantity(int portfolioId) {
        String query = "SELECT quantity FROM Portfolio WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, portfolioId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        return -1; // Return -1 if the portfolio item is not found or an error occurs
    }

    public PortfolioItem getEarliestMatchingPortfolioItem(int userId, String stockSymbol, int quantity,double desiredPrice) {
        String query = "SELECT * FROM Portfolio WHERE user_id = ? AND quantity >= ? AND stock_symbol = ? AND purchase_price = ? ORDER BY purchase_date ASC LIMIT 1";
        StockTableOperations stockTableOperations = new StockTableOperations(connection);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, quantity);
            statement.setString(3, stockSymbol);
            statement.setDouble(4, desiredPrice);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int portfolioId = resultSet.getInt("id");
                    double purchasePrice = resultSet.getDouble("purchase_price");
                    int retrievedQuantity = resultSet.getInt("quantity");

                    Stock stock = stockTableOperations.getStock(stockSymbol);

                    return new PortfolioItem(portfolioId, stock, retrievedQuantity, purchasePrice);
                }
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        return null; // Return null if no matching portfolio item is found
    }

//    public PortfolioItem getEarliestMatchingSellPortfolioItem(int userId, String stockSymbol, int quantity,double desiredPrice) {
//        String query = "SELECT * FROM Portfolio WHERE user_id = ? AND quantity <= ? AND stock_symbol = ? AND purchase_price = ? ORDER BY purchase_date ASC LIMIT 1";
//        StockTableOperations stockTableOperations = new StockTableOperations(connection);
//
//        try (PreparedStatement statement = connection.prepareStatement(query)) {
//            statement.setInt(1, userId);
//            statement.setInt(2, quantity);
//            statement.setString(3, stockSymbol);
//            statement.setDouble(4, desiredPrice);
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    int portfolioId = resultSet.getInt("id");
//                    double purchasePrice = resultSet.getDouble("purchase_price");
//                    int retrievedQuantity = resultSet.getInt("quantity");
//
//                    Stock stock = stockTableOperations.getStock(stockSymbol);
//
//                    return new PortfolioItem(portfolioId, stock, retrievedQuantity, purchasePrice);
//                }
//            }
//        } catch (SQLException e) {
//            // Handle any potential exceptions
//            e.printStackTrace();
//        }
//
//        return null; // Return null if no matching portfolio item is found
//    }

    public PortfolioItem getEarliestMatchingSellPortfolioItem(int userId, String stockSymbol,double desiredPrice) {
        String query = "SELECT * FROM Portfolio WHERE user_id = ? AND stock_symbol = ? AND purchase_price = ? ORDER BY purchase_date ASC LIMIT 1";
        StockTableOperations stockTableOperations = new StockTableOperations(connection);

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);
            statement.setDouble(3, desiredPrice);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int portfolioId = resultSet.getInt("id");
                    double purchasePrice = resultSet.getDouble("purchase_price");
                    int retrievedQuantity = resultSet.getInt("quantity");

                    Stock stock = stockTableOperations.getStock(stockSymbol);

                    return new PortfolioItem(portfolioId, stock, retrievedQuantity, purchasePrice);
                }
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        return null; // Return null if no matching portfolio item is found
    }

    public int getTotalStockQuantity(int userId, String stockSymbol, double stockPrice) throws SQLException {
        String query = "SELECT SUM(quantity) FROM Portfolio WHERE user_id = ? AND stock_symbol = ? AND purchase_price = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);
            statement.setDouble(3, stockPrice);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0; // Return 0 if no result found
    }

    public List<PortfolioItem> getUserPortfolio(int userId) throws SQLException {
        List<PortfolioItem> portfolio = new ArrayList<>();
        StockTableOperations stockTableOperations = new StockTableOperations(connection);

        String query = "SELECT * FROM Portfolio WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int portfolioId = resultSet.getInt("id");
                String stockSymbol = resultSet.getString("stock_symbol");
                int quantity = resultSet.getInt("quantity");
                double purchasePrice = resultSet.getDouble("purchase_price");

                Stock stock = stockTableOperations.getStock(stockSymbol);

                PortfolioItem portfolioItem = new PortfolioItem(portfolioId, stock , quantity, purchasePrice);
                portfolio.add(portfolioItem);
            }
        }

        return portfolio;
    }
    // Other methods for portfolio-related operations...
}
