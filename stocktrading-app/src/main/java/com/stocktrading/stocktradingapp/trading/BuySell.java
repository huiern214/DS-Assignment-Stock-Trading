package com.stocktrading.stocktradingapp.trading;

import com.stocktrading.stocktradingapp.databaseOperations.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BuySell {
    private Connection connection;
    private UsersTableOperations usersTableOperations;
    private PortfolioTableOperations portfolioTableOperations;
    private StockTableOperations stockTableOperations;
    private OrdersTableOperations ordersTableOperations;
    private TransactionsTableOperations transactionsTableOperations;

    public BuySell(Connection connection) {
        this.connection = connection;
        this.usersTableOperations = new UsersTableOperations(connection);
        this.portfolioTableOperations = new PortfolioTableOperations(connection);
        this.stockTableOperations = new StockTableOperations(connection);
        this.ordersTableOperations = new OrdersTableOperations(connection);
        this.transactionsTableOperations = new TransactionsTableOperations(connection);
    }

    public void buyStock(String stockSymbol, double desiredPrice, int desiredQuantity, int buyerId) throws SQLException {
        // Step 2: Check if the desired price is within 10% of the current price
        Stock stock = stockTableOperations.getStock(stockSymbol);
        double currentPrice = stock.getCurrentPrice();
        double priceThreshold = currentPrice * 0.1;

        if (Math.abs(desiredPrice - currentPrice) > priceThreshold) {
            throw new IllegalArgumentException("Desired price is not within 10% of the current price.");
        }

        // Step 3: Check if the buyer's balance is sufficient for the transaction
        double buyerBalance = usersTableOperations.getUserFunds(buyerId);

        double totalPrice = desiredPrice * desiredQuantity;
        if (buyerBalance < totalPrice) {
            throw new IllegalArgumentException("Insufficient funds to buy the stock.");
        }

        // Step 4: Check if there is a matching order in the Order table
        List<Order> matchingOrders = ordersTableOperations.getMatchingSellOrders(stockSymbol, desiredPrice, buyerId);
        int leftoverStockQuantity = desiredQuantity;
        int totalQuantityInMatchingOrders = 0;
        int tempSystemStockQuantity = stockTableOperations.getStockQuantity(stockSymbol);
        //Check if the matchingOrders meets the desiredQuantity of stocks to be purchased
        for (Order matchingOrder : matchingOrders){
            totalQuantityInMatchingOrders = totalQuantityInMatchingOrders + matchingOrder.getQuantity();
        }

        //Check if the amount of stock in the system and order is sufficient for the buyer's desired amount
        if (!(tempSystemStockQuantity + totalQuantityInMatchingOrders >= desiredQuantity)){
            return;
        }

        if ((matchingOrders != null) && (matchingOrders.size() != 0)) {
            for (Order matchingOrder : matchingOrders) {
                //When there are no more stock to buy, it will break out of the loop
                if (leftoverStockQuantity <= 0) { break;}

                // Step 5: Process the trade with each matching order
                int sellerId = matchingOrder.getUserId();
                int sellerQuantity = matchingOrder.getQuantity();
                int orderId = matchingOrder.getOrderId();
                double sellerPrice = matchingOrder.getPrice();

                if (sellerQuantity >= leftoverStockQuantity) {
                    // Step 5.1: Execute the full trade
                    //This method will affect the seller's side
                    executeTrade(stockSymbol, desiredPrice, leftoverStockQuantity, sellerId, sellerPrice,
                            sellerQuantity, orderId);
                    //These methods will affect the buyer's side
                    // Update buyer's funds
                    usersTableOperations.updateUserFunds(buyerId, (usersTableOperations.getUserFunds(buyerId)) - (leftoverStockQuantity * sellerPrice));

                    // Add stock to buyer's portfolio
                    portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);

                    // Add transaction for the buyer
                    transactionsTableOperations.insertTransaction(buyerId, stockSymbol, desiredPrice, leftoverStockQuantity, "BUY");
                    //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                    leftoverStockQuantity = sellerQuantity - leftoverStockQuantity;

                } else {
                    // Step 5.2: Execute a partial trade
                    executePartialTrade(stockSymbol, desiredPrice, leftoverStockQuantity, sellerId, sellerPrice,
                            sellerQuantity, orderId);
                    //These methods will affect the buyer's side
                    // Update buyer's funds
                    usersTableOperations.updateUserFunds(buyerId, (usersTableOperations.getUserFunds(buyerId)) - (sellerQuantity * sellerPrice));

                    // Add stock to buyer's portfolio
                    portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, sellerQuantity, desiredPrice);

                    // Add transaction for the buyer
                    transactionsTableOperations.insertTransaction(buyerId, stockSymbol, desiredPrice, sellerQuantity, "BUY");
                    //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                    leftoverStockQuantity = leftoverStockQuantity - sellerQuantity;
                }

            }
        } else {
            // Step 4.5: Buy stock from the system stock

            int systemStockQuantity = stockTableOperations.getStockQuantity(stockSymbol);

            // Happens when there are no system stock available, so an Order will be placed in the Orders Table to see
            if (systemStockQuantity < desiredQuantity) {
                ordersTableOperations.insertOrder(buyerId, stockSymbol, "BUY",desiredQuantity, desiredPrice);
                return;
            }

            double systemStockPrice = stock.getCurrentPrice();

            // Update buyer's funds
            usersTableOperations.updateUserFunds(buyerId, (usersTableOperations.getUserFunds(buyerId)) - totalPrice);

            // Add stock to buyer's portfolio
            portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, desiredQuantity, desiredPrice);

            // Add transaction for the buyer
            transactionsTableOperations.insertTransaction(buyerId, stockSymbol, desiredPrice, desiredQuantity, "BUY");

            // Update system stock quantity
            stockTableOperations.updateStockQuantity(stockSymbol, systemStockQuantity-desiredQuantity);
            return;
        }

        //If there are Matching Orders, but after buying from Orders, there are still leftovers, then it will buy from system stock
        if (leftoverStockQuantity > 0) {
            // Step 4.5: Buy stock from the system stock
            int systemStockQuantity = stockTableOperations.getStockQuantity(stockSymbol);

            // Update buyer's funds
            usersTableOperations.updateUserFunds(buyerId, (usersTableOperations.getUserFunds(buyerId)) - (desiredPrice * leftoverStockQuantity));

            // Add stock to buyer's portfolio
            portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);

            // Add transaction for the buyer
            transactionsTableOperations.insertTransaction(buyerId, stockSymbol, desiredPrice, leftoverStockQuantity, "BUY");

            // Update system stock quantity
            stockTableOperations.updateStockQuantity(stockSymbol, systemStockQuantity - leftoverStockQuantity);
        }
    }

    private void executeTrade(String stockSymbol, double price, int quantity, int sellerId, double sellerPrice,
                              int sellerQuantity, int orderId) throws SQLException {

        // Execute the trade by updating necessary tables and performing the transaction
        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
        PortfolioItem portfolioitem = portfolioTableOperations.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity,price);
        int portfolioId = portfolioitem.getPortfolioId();
        portfolioTableOperations.updateStockQuantity(portfolioId, stockSymbol, sellerQuantity - quantity);

        //Checks and remove the items in the portfolio that have 0 quantity
        if (portfolioTableOperations.getPortfolioQuantity(portfolioId) <= 0){
            portfolioTableOperations.removeStockFromPortfolioByPortfolioId(portfolioId);
        }

        // Deduct the stock quantity from the seller's available quantity in the Orders table
        ordersTableOperations.updateOrderQuantity(orderId, sellerQuantity - quantity);

        //Checks and remove if that item has 0 quantity
        if (ordersTableOperations.getOrderQuantity(orderId) <= 0){
            ordersTableOperations.removeOrderByOrderId(orderId);
        }

        // Deduct the stock quantity * price from the seller's funds in the Users table
        double sellerBalance = usersTableOperations.getUserFunds(sellerId);
        usersTableOperations.updateUserFunds(sellerId, sellerBalance + (quantity * sellerPrice));

        // Insert the trade details into the Transactions table for the seller
        transactionsTableOperations.insertTransaction(sellerId, stockSymbol, price, quantity, "SELL");
    }

    private void executePartialTrade(String stockSymbol, double price, int quantity, int sellerId, double sellerPrice,
                                     int sellerQuantity, int orderId) throws SQLException {
        // Execute a partial trade when the matched selling order has a lesser quantity than the desired quantity
        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
        PortfolioItem portfolioitem = portfolioTableOperations.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity,price);
        int portfolioId = portfolioitem.getPortfolioId();
        portfolioTableOperations.updateStockQuantity(portfolioId, stockSymbol, 0);

        //Checks and remove the items in the portfolio that have 0 quantity
        if (portfolioTableOperations.getPortfolioQuantity(portfolioId) <= 0){
            portfolioTableOperations.removeStockFromPortfolioByPortfolioId(portfolioId);
        }

        // Deduct the stock quantity from the seller's available quantity in the Orders table
        ordersTableOperations.updateOrderQuantity(orderId, 0);

        //Checks and remove if that item has 0 quantity
        if (ordersTableOperations.getOrderQuantity(orderId) <= 0){
            ordersTableOperations.removeOrderByOrderId(orderId);
        }

        // Deduct the stock quantity * price from the buyer's funds in the Users table
        double sellerBalance = usersTableOperations.getUserFunds(sellerId);
        usersTableOperations.updateUserFunds(sellerId, sellerBalance + (sellerQuantity * sellerPrice));

        // Add the stock to the buyer's portfolio in the Portfolio table with the purchased quantity
//        portfolioTableOperations.addStockToPortfolio(sellerId, stockSymbol, sellerQuantity, price);

        // Insert the trade details into the Transactions table
        transactionsTableOperations.insertTransaction(sellerId, stockSymbol, price, sellerQuantity, "SELL");


    }

    public void sellStock(String stockSymbol, double desiredPrice, int desiredQuantity, int sellerId) throws SQLException {
        // Step 1: Check if the seller owns enough stock in the portfolio
        int portfolioQuantity = portfolioTableOperations.getTotalStockQuantity(sellerId, stockSymbol, desiredPrice);
        if (portfolioQuantity < desiredQuantity){
            throw new IllegalArgumentException("Seller does not own enough stock to sell.");
        }

        // Step 2: Check if the desired price is within 10% of the current price
        Stock stock = stockTableOperations.getStock(stockSymbol);
        double currentPrice = stock.getCurrentPrice();
        double priceThreshold = currentPrice * 0.1;

        if (Math.abs(desiredPrice - currentPrice) > priceThreshold) {
            throw new IllegalArgumentException("Desired price is not within 10% of the current price.");
        }

        // Step 4: Check if there is a matching order in the Order table
        List<Order> matchingOrders = ordersTableOperations.getMatchingBuyOrders(stockSymbol, desiredPrice,sellerId);

        int leftoverStockQuantity = desiredQuantity;
        int totalQuantityInMatchingOrders = 0;
        //Check if the matchingOrders meets the desiredQuantity of stocks to be sold
        for (Order matchingOrder : matchingOrders){
            totalQuantityInMatchingOrders = totalQuantityInMatchingOrders + matchingOrder.getQuantity();
        }
        //If there are matchingOrders then proceed to sell those first
        if ((matchingOrders != null) && (matchingOrders.size() != 0)) {
            for (Order matchingOrder : matchingOrders) {

                //When there are no more stock needed to sell, it will break out of the loop
                if (leftoverStockQuantity <= 0) { break;}

                // Step 5: Process the trade with each matching order
                int buyerId = matchingOrder.getUserId();
                int buyerQuantity = matchingOrder.getQuantity();
                int orderId = matchingOrder.getOrderId();
                double buyerPrice = matchingOrder.getPrice();

                //If buyer's Order isn't 0, then it will keep looping until either the seller sold enough, or the buyer's Order is 0, then only move to the next Order.
                while (buyerQuantity != 0) {
                    //get the earliest portfolio quantity in the seller's portfolio
                    PortfolioItem firstPortfolio = portfolioTableOperations.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                    int firstPortfolioQuantity = firstPortfolio.getQuantity();

                    if (buyerQuantity >= firstPortfolioQuantity) {
                        // Step 5.1: Execute the full trade
                        //This method will affect the buyer's side
                        executeSellTrade(stockSymbol, desiredPrice, firstPortfolioQuantity, buyerId, buyerPrice,
                                buyerQuantity, orderId);

                        //These methods will affect the seller's side
                        // Update seller's funds
                        usersTableOperations.updateUserFunds(sellerId, (usersTableOperations.getUserFunds(sellerId)) + (firstPortfolioQuantity * desiredPrice));

                        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
                        PortfolioItem portfolioitem = portfolioTableOperations.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                        int portfolioId = portfolioitem.getPortfolioId();
                        portfolioTableOperations.updateStockQuantity(portfolioId, stockSymbol, 0);

                        //Checks and remove the items in the portfolio that have 0 quantity
                        if (portfolioTableOperations.getPortfolioQuantity(portfolioId) <= 0) {
                            portfolioTableOperations.removeStockFromPortfolioByPortfolioId(portfolioId);
                        }

                        // Add transaction for the seller
                        transactionsTableOperations.insertTransaction(sellerId, stockSymbol, desiredPrice, firstPortfolioQuantity, "SELL");

                        //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                        leftoverStockQuantity = buyerQuantity - firstPortfolioQuantity;

                        //Since buyerQuantity will be bigger or equal than the quantity in the portfolio, so there are still some quantity left to be bought in the Order
                        buyerQuantity = buyerQuantity - firstPortfolioQuantity;

                    } else {
                        // Step 5.2: Execute a partial trade
                        executePartialSellTrade(stockSymbol, desiredPrice, firstPortfolioQuantity, buyerId, buyerPrice,
                                buyerQuantity, orderId);

                        //These methods will affect the seller's side
                        // Update seller's funds
                        usersTableOperations.updateUserFunds(sellerId, (usersTableOperations.getUserFunds(sellerId)) + (buyerQuantity * desiredPrice));

                        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
                        PortfolioItem portfolioitem = portfolioTableOperations.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                        int portfolioId = portfolioitem.getPortfolioId();
                        portfolioTableOperations.updateStockQuantity(portfolioId, stockSymbol, (firstPortfolioQuantity - buyerQuantity));

                        //Checks and remove the items in the portfolio that have 0 quantity
                        if (portfolioTableOperations.getPortfolioQuantity(portfolioId) <= 0) {
                            portfolioTableOperations.removeStockFromPortfolioByPortfolioId(portfolioId);
                        }

                        // Add transaction for the seller
                        transactionsTableOperations.insertTransaction(sellerId, stockSymbol, desiredPrice, buyerQuantity, "SELL");

                        //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                        leftoverStockQuantity = leftoverStockQuantity - buyerQuantity;
                        buyerQuantity = 0;

                    }
                }
            }
        } else {
            // Step 4.5: Place a new "SELL" order in the Orders table
            ordersTableOperations.insertOrder(sellerId, stockSymbol, "SELL", leftoverStockQuantity, desiredPrice);
        }

        //If there are still leftover quantity to be sold, then the remaining will be placed as an Order
        if (leftoverStockQuantity > 0){
            ordersTableOperations.insertOrder(sellerId, stockSymbol, "SELL", leftoverStockQuantity, desiredPrice);
        }

    }

    //Only used in selling stock to update the buyer's side of the trade when the quantity in the buyer's order is bigger than the seller's first portfolio amount
    public void executeSellTrade(String stockSymbol, double tradePrice, int tradeQuantity, int buyerId, double buyerPrice,
                             int buyerQuantity, int orderId) throws SQLException {
        // Calculate the total trade value
        double totalTradeValue = tradePrice * tradeQuantity;

        // Update buyer's funds
        double buyerFunds = usersTableOperations.getUserFunds(buyerId);
        double updatedBuyerFunds = buyerFunds - totalTradeValue;
        usersTableOperations.updateUserFunds(buyerId, updatedBuyerFunds);

        // Deduct the stock quantity from the buyer's available quantity in the Orders table
        ordersTableOperations.updateOrderQuantity(orderId, buyerQuantity - tradeQuantity);

        //Checks and remove if that item has 0 quantity in Orders table
        if (ordersTableOperations.getOrderQuantity(orderId) <= 0){
            ordersTableOperations.removeOrderByOrderId(orderId);
        }

        // Add stock to buyer's portfolio
        portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, tradeQuantity, tradePrice);

        // Add transaction for the buyer
        transactionsTableOperations.insertTransaction(buyerId, stockSymbol, tradePrice, tradeQuantity, "BUY");

    }

    //Only used in selling stock to update the buyer's side of the trade when the quantity in the buyer's order is smaller than the seller's first portfolio amount
    public void executePartialSellTrade(String stockSymbol, double tradePrice, int tradeQuantity, int buyerId, double buyerPrice,
                                    int buyerQuantity, int orderId) throws SQLException {
        // Calculate the total trade value
        double totalTradeValue = tradePrice * buyerQuantity;

        // Update seller's funds
        double buyerFunds = usersTableOperations.getUserFunds(buyerId);
        double updatedBuyerFunds = buyerFunds - totalTradeValue;
        usersTableOperations.updateUserFunds(buyerId, updatedBuyerFunds);

        // Deduct the stock quantity from the buyer's available quantity in the Orders table
        ordersTableOperations.updateOrderQuantity(orderId,  0);

        //Checks and remove if that item has 0 quantity in Orders table
        if (ordersTableOperations.getOrderQuantity(orderId) <= 0){
            ordersTableOperations.removeOrderByOrderId(orderId);
        }

        // Add stock to buyer's portfolio
        portfolioTableOperations.addStockToPortfolio(buyerId, stockSymbol, buyerQuantity, tradePrice);

        // Add transaction for the buyer
        transactionsTableOperations.insertTransaction(buyerId, stockSymbol, tradePrice, buyerQuantity, "BUY");
    }
}
