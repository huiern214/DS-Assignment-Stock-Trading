package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.*;
import java.util.List;

public class MatchOrders {

    private Connection connection;
    private OrdersTableOperations ordersTableOperations;
    private UsersTableOperations usersTableOperations;
    private StockTableOperations stockTableOperations;
    private PortfolioTableOperations portfolioTableOperations;
    private TransactionsTableOperations transactionsTableOperations;

    public MatchOrders(Connection connection) {
        this.connection = connection;
        this.ordersTableOperations = new OrdersTableOperations(connection);
        this.usersTableOperations = new UsersTableOperations(connection);
        this.stockTableOperations = new StockTableOperations(connection);
        this.portfolioTableOperations = new PortfolioTableOperations(connection);
        this.transactionsTableOperations = new TransactionsTableOperations(connection);
    }

    public void fulfillOrders() throws SQLException {
        // Run checkOrders to see if any BUY orders in the system, and return a list of
        // Orders
        List<Order> matchingOrders = ordersTableOperations.getMatchingOrders();

        // Check each order if it matches the new price
        for (Order order : matchingOrders) {
            // Set variables for the order
            int orderId = order.getOrderId();
            int buyerId = order.getUserId();
            String stockSymbol = order.getStockSymbol();
            int desiredQuantity = order.getQuantity();
            double desiredPrice = order.getPrice();

            // Check the system stock for quantity
            int systemStockQuantity = stockTableOperations.getStockQuantity(stockSymbol);
            // Happens when there are no system stock available
            if (systemStockQuantity <= 0) {
                throw new IllegalArgumentException("Insufficient system stock quantity");
            }

            // set the trade quantity to whichever is smaller, the system stock, or the
            // quantity of the users order
            int tradeQuantity = Math.min(desiredQuantity, systemStockQuantity);

            // Check the system stock for the price
            Stock systemStock = stockTableOperations.getStock(stockSymbol);
            double systemStockPrice = systemStock.getCurrentPrice();

            // Check if the buyer's balance is sufficient for the transaction
            double buyerBalance = usersTableOperations.getUserFunds(buyerId);

            double totalPrice = desiredPrice * tradeQuantity;
            if (buyerBalance < totalPrice) {
                throw new IllegalArgumentException("Insufficient funds to buy the stock.");
            }

            // If the new price matches the desired buying price of the user, then the order
            // will be fulfilled
            if (desiredPrice == systemStockPrice) {
                // Deduct the stock quantity from the buyer's available quantity in the Orders
                // table
                ordersTableOperations.updateOrderQuantity(orderId, desiredQuantity - tradeQuantity);

                // Checks and remove if that item has 0 quantity in Orders table
                if (ordersTableOperations.getOrderQuantity(orderId) <= 0) {
                    ordersTableOperations.removeOrderByOrderId(orderId);
                }

                // Update buyer's funds
                usersTableOperations.updateUserFunds(buyerId,
                        (usersTableOperations.getUserFunds(buyerId)) - totalPrice);

                // Add stock to buyer's portfolio
                portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, tradeQuantity, desiredPrice);

                // Add transaction for the buyer
                transactionsTableOperations.insertTransaction(buyerId, stockSymbol, desiredPrice, tradeQuantity, "BUY");

                // Update system stock quantity
                stockTableOperations.updateStockQuantity(stockSymbol, systemStockQuantity - tradeQuantity);

            }
        }
    }

}
