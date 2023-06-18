package com.stocktrading.stocktradingapp.service;

import java.sql.*;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Order;
import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.databaseOperations.OrdersTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.PortfolioTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.StockTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.TransactionsTableOperationService;

@Service
public class MatchOrdersService {

    private final OrdersTableOperationService ordersTableOperationService;
    private final UserService userService;
    private final StockTableOperationService stockTableOperationService;
    private final PortfolioTableOperationService portfolioTableOperationService;
    private final TransactionsTableOperationService transactionsTableOperationService;

    public MatchOrdersService(OrdersTableOperationService ordersTableOperationService, UserService userService, StockTableOperationService stockTableOperationService, PortfolioTableOperationService portfolioTableOperationService, TransactionsTableOperationService transactionsTableOperationService){
        this.ordersTableOperationService = ordersTableOperationService;
        this.userService = userService;
        this.stockTableOperationService = stockTableOperationService;
        this.portfolioTableOperationService = portfolioTableOperationService;
        this.transactionsTableOperationService = transactionsTableOperationService;
    }

    public void fulfillOrders() throws SQLException {
        // Run checkOrders to see if any BUY orders in the system, and return a list of Orders
        List<Order> matchingOrders = ordersTableOperationService.getMatchingOrders();

        // Check each order if it matches the new price
        for (Order order : matchingOrders) {
            int orderId = order.getOrderId();
            int buyerId = order.getUserId();
            String stockSymbol = order.getStockSymbol();
            int desiredQuantity = order.getQuantity();
            double desiredPrice = order.getPrice();

            // Check the system stock for quantity
            int systemStockQuantity = stockTableOperationService.getStockQuantity(stockSymbol);
            // Happens when there are no system stock available
            if (systemStockQuantity <= 0) {
                throw new IllegalArgumentException("Insufficient system stock quantity");
            }

            // set the trade quantity to whichever is smaller, the system stock, or the
            // quantity of the users order
            int tradeQuantity = Math.min(desiredQuantity, systemStockQuantity);

            // Check the system stock for the price
            Stock systemStock = stockTableOperationService.getStock(stockSymbol);
            double systemStockPrice = systemStock.getPrice();

            // Check if the buyer's balance is sufficient for the transaction
            double buyerBalance = userService.getUserFunds(buyerId);

            double totalPrice = desiredPrice * tradeQuantity;
            if (buyerBalance < totalPrice) {
                throw new IllegalArgumentException("Insufficient funds to buy the stock.");
            }

            // If the d buying price of the user, then the order
            // will be fulfilled
            if (desiredPrice == systemStockPrice) {
                // Deduct the stock quantity from the buyer's available quantity in the Orders
                // table
                ordersTableOperationService.updateOrderQuantity(orderId, desiredQuantity - tradeQuantity);

                // Checks and remove if that item has 0 quantity in Orders table
                if (ordersTableOperationService.getOrderQuantity(orderId) <= 0) {
                    ordersTableOperationService.removeOrderByOrderId(orderId);
                }

                // Update buyer's funds
                userService.updateUserFunds(buyerId,
                        (userService.getUserFunds(buyerId)) - totalPrice);

                // Add stock to buyer's portfolio
                portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, tradeQuantity, desiredPrice);

                // Add transaction for the buyer
                transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, desiredPrice, tradeQuantity, "BUY");

                // Update system stock quantity
                stockTableOperationService.updateStockQuantity(stockSymbol, systemStockQuantity - tradeQuantity);

            }
        }
    }

}

