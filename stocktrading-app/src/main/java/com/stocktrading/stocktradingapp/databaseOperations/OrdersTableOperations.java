package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersTableOperations {
    private Connection connection;

    public OrdersTableOperations(Connection connection) {
        this.connection = connection;
    }

    public int insertOrder(int userId, String stockSymbol, String orderType, int quantity, double price) throws SQLException {
        String insertOrderQuery = "INSERT INTO Orders (user_id, stock_symbol, order_type, quantity, price) VALUES (?, ?, ?, ?, ?)";
        int orderId = -1;

        try (PreparedStatement statement = connection.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);
            statement.setString(3, orderType);
            statement.setInt(4, quantity);
            statement.setDouble(5, price);

            statement.executeUpdate();

            // Get the generated order ID
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }
        }

        return orderId;
    }

    public void updateOrderQuantity(int orderId, int newQuantity) throws SQLException {
        String query = "UPDATE Orders SET quantity = ? WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newQuantity);
            statement.setInt(2, orderId);

            statement.executeUpdate();
            }

    }

    public List<Order> getMatchingSellOrders(String stockSymbol, double price, int user_id) throws SQLException {
        String query = "SELECT * FROM Orders WHERE stock_symbol = ? AND order_type = 'SELL' AND price <= ? AND user_id != ? ORDER BY timestamp ASC";

        List<Order> sellOrders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockSymbol);
            statement.setDouble(2, price);
            statement.setInt(3, user_id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    int userId = resultSet.getInt("user_id");
                    int quantity = resultSet.getInt("quantity");
                    String orderType = resultSet.getString("order_type");

                    Order order = new Order(orderId, userId, stockSymbol, quantity, price, orderType);
                    sellOrders.add(order);
                }
            }
        }

        return sellOrders;
    }

    public List<Order> getMatchingBuyOrders(String stockSymbol, double price, int user_id) throws SQLException {
        String query = "SELECT * FROM Orders WHERE stock_symbol = ? AND order_type = 'BUY' AND price <= ? AND user_id != ?ORDER BY timestamp ASC";

        List<Order> sellOrders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, stockSymbol);
            statement.setDouble(2, price);
            statement.setInt(3, user_id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    int userId = resultSet.getInt("user_id");
                    int quantity = resultSet.getInt("quantity");
                    String orderType = resultSet.getString("order_type");

                    Order order = new Order(orderId, userId, stockSymbol, quantity, price, orderType);
                    sellOrders.add(order);
                }
            }
        }

        return sellOrders;
    }

    public List<Order> getMatchingOrders() throws SQLException {
        String query = "SELECT * FROM Orders WHERE order_type = 'BUY'  ORDER BY timestamp ASC";

        List<Order> sellOrders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("order_id");
                    int userId = resultSet.getInt("user_id");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    String orderType = resultSet.getString("order_type");

                    Order order = new Order(orderId, userId, stockSymbol, quantity, price, orderType);
                    sellOrders.add(order);
                }
            }
        }

        return sellOrders;
    }

    public int getOrderQuantity(int orderId) {
        String query = "SELECT quantity FROM Orders WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }

        return -1; // Return -1 if the order is not found or an error occurs
    }

    public void removeOrderByOrderId(int orderId) {
        String query = "DELETE FROM Orders WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }
    }
    // Other methods for retrieving orders, updating order details, etc.
}
