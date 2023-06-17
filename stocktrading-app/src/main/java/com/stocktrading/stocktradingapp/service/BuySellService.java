package com.stocktrading.stocktradingapp.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.databaseOperations.OrdersTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.PortfolioTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.StockTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.TransactionsTableOperationService;
import com.stocktrading.stocktradingapp.model.Order;
import com.stocktrading.stocktradingapp.model.PortfolioItem;

@Service
public class BuySellService {

    private final UserService userService;
    private final PortfolioTableOperationService portfolioTableOperationService;
    private final StockTableOperationService stockTableOperationService;
    private final OrdersTableOperationService ordersTableOperationService;
    private final TransactionsTableOperationService transactionsTableOperationService;

    public BuySellService(UserService userService, PortfolioTableOperationService portfolioTableOperationService, StockTableOperationService stockTableOperationService, OrdersTableOperationService ordersTableOperationService, TransactionsTableOperationService transactionsTableOperationService) throws SQLException {
        this.userService = userService;
        this.portfolioTableOperationService = portfolioTableOperationService;
        this.stockTableOperationService = stockTableOperationService;
        this.ordersTableOperationService = ordersTableOperationService;
        this.transactionsTableOperationService = transactionsTableOperationService;
    }

    public void buyStock(String stockSymbol, double desiredPrice, int desiredQuantity, int buyerId) throws SQLException {
        // Step 2: Check if the desired price is within 10% of the current price
        Stock stock = stockTableOperationService.getStock(stockSymbol);
        double currentPrice = stock.getPrice();
        double priceThreshold = currentPrice * 0.1;

        if (Math.abs(desiredPrice - currentPrice) > priceThreshold) {
            throw new IllegalArgumentException("Desired price is not within 10% of the current price.");
        }

        // Step 3: Check if the buyer's balance is sufficient for the transaction
        double buyerBalance = userService.getUserFunds(buyerId);

        double totalPrice = desiredPrice * desiredQuantity * 100; // 100 shares per lot
        if (buyerBalance < totalPrice) {
            throw new IllegalArgumentException("Insufficient funds to buy the stock.");
        }

        // Step 4: Check if there is a matching order in the Order table
        List<Order> matchingOrders = ordersTableOperationService.getMatchingSellOrders(stockSymbol, desiredPrice, buyerId);
        int leftoverStockQuantity = desiredQuantity;
        int totalQuantityInMatchingOrders = 0;
        // int tempSystemStockQuantity = stockTableOperationService.getStockQuantity(stockSymbol);
        // double tempSystemStockPrice = stockTableOperationService.getStock(stockSymbol).getPrice();
        //Check if the matchingOrders meets the desiredQuantity of stocks to be purchased
        for (Order matchingOrder : matchingOrders){
            totalQuantityInMatchingOrders = totalQuantityInMatchingOrders + matchingOrder.getQuantity();
        }
        //Check if the amount of stock in the system and order is sufficient for the buyer's desired amount
        //If there are not enough amount in the system plus portfolio of sellers, then it will be listed as an Order.
        // if (tempSystemStockPrice == desiredPrice){
        //     if (!(tempSystemStockQuantity + totalQuantityInMatchingOrders >= desiredQuantity)){
        //         ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
        //         // return;
        //         throw new IllegalArgumentException("Not enough stock for the desired price. The order has been added. Please wait for a matching order.");
        //     }
        // }
        // else {
        //     if (!(totalQuantityInMatchingOrders >= desiredQuantity)){
        //         ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
        //         // return;
        //         throw new IllegalArgumentException("Not enough stock for the desired price. The order has been added. Please wait for a matching order.");
        //     }
        // }

        // if (!(tempSystemStockQuantity + totalQuantityInMatchingOrders >= desiredQuantity)){
        //     ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
        //     return;
        // }

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
                    userService.updateUserFunds(buyerId, (userService.getUserFunds(buyerId)) - (leftoverStockQuantity * sellerPrice * 100)); // 100 shares per lot

                    // Add stock to buyer's portfolio
                    // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);
                    portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);

                    // Add transaction for the buyer
                    transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, desiredPrice, leftoverStockQuantity, "BUY");
                    //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                    // leftoverStockQuantity = sellerQuantity - leftoverStockQuantity;
                    leftoverStockQuantity -= sellerQuantity;
                } else {
                    // Step 5.2: Execute a partial trade
                    executePartialTrade(stockSymbol, desiredPrice, leftoverStockQuantity, sellerId, sellerPrice,
                            sellerQuantity, orderId);
                    //These methods will affect the buyer's side
                    // Update buyer's funds
                    userService.updateUserFunds(buyerId, (userService.getUserFunds(buyerId)) - (sellerQuantity * sellerPrice * 100)); // 100 shares per lot

                    // Add stock to buyer's portfolio
                    // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, sellerQuantity, desiredPrice);
                    portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);

                    // Add transaction for the buyer
                    transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, desiredPrice, sellerQuantity, "BUY");
                    //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                    leftoverStockQuantity = leftoverStockQuantity - sellerQuantity;
                }

            }
        } else {
            // Step 4.5: Buy stock from the system stock
            int systemStockQuantity = stockTableOperationService.getStockQuantity(stockSymbol);
            double systemStockPrice = stockTableOperationService.getStock(stockSymbol).getPrice();

            // Check if the system stock price is the same as the desired price
            if (systemStockPrice != desiredPrice) {
                ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
                throw new IllegalArgumentException("Not enough stock for the desired price. The order has been added. Please wait for a matching order.");
            }

            // Happens when there are no system stock available, so an Order will be placed in the Orders Table to see
            if (systemStockQuantity < desiredQuantity) {
                ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
                throw new IllegalArgumentException("Not enough stock for the desired price. The order has been added. Please wait for a matching order.");
            }

            // double systemStockPrice = stock.getPrice();

            // Update buyer's funds
            userService.updateUserFunds(buyerId, (userService.getUserFunds(buyerId)) - totalPrice);

            // Add stock to buyer's portfolio
            // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, desiredQuantity, desiredPrice);
            portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, desiredQuantity, desiredPrice);
            
            // Add transaction for the buyer
            transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, desiredPrice, desiredQuantity, "BUY");

            // Update system stock quantity
            stockTableOperationService.updateStockQuantity(stockSymbol, systemStockQuantity-desiredQuantity);
            return;
        }

        //If there are Matching Orders, but after buying from Orders, there are still leftovers, then it will buy from system stock
        if (leftoverStockQuantity > 0) {
            // Step 4.5: Buy stock from the system stock
            int systemStockQuantity = stockTableOperationService.getStockQuantity(stockSymbol);
            double systemStockPrice = stockTableOperationService.getStock(stockSymbol).getPrice();

            // Check if the system stock price is the same as the desired price
            if (systemStockPrice != desiredPrice) {
                ordersTableOperationService.insertOrder(buyerId, stockSymbol, "BUY", desiredQuantity, desiredPrice);
                throw new IllegalArgumentException("Not enough stock for the desired price. The order has been added. Please wait for a matching order.");
            }

            // Update buyer's funds
            userService.updateUserFunds(buyerId, (userService.getUserFunds(buyerId)) - (desiredPrice * leftoverStockQuantity * 100)); // 100 shares per lot

            // Add stock to buyer's portfolio
            // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);
            portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, leftoverStockQuantity, desiredPrice);

            // Add transaction for the buyer
            transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, desiredPrice, leftoverStockQuantity, "BUY");

            // Update system stock quantity
            stockTableOperationService.updateStockQuantity(stockSymbol, systemStockQuantity - leftoverStockQuantity);
        }
    }

    private void executeTrade(String stockSymbol, double price, int quantity, int sellerId, double sellerPrice,
                              int sellerQuantity, int orderId) throws SQLException {

        // Execute the trade by updating necessary tables and performing the transaction
        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity, price);
        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity);
        PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol);
        if (portfolioitem == null) {
            throw new IllegalArgumentException("Portfolio item not found");
        }
        int portfolioId = portfolioitem.getPortfolioId();
        int portfolioQuantity = portfolioitem.getQuantity();
        portfolioTableOperationService.updateStockQuantity(portfolioId, stockSymbol, portfolioQuantity - quantity);
        System.out.println("Sellerquantity: " + sellerQuantity + " TradeQuantity: " + quantity);
        System.out.println("PortfolioId: " + portfolioId + " StockSymbol: " + stockSymbol + " SellerQuantity - TradeQuantity: " + (sellerQuantity - quantity));

        //Checks and remove the items in the portfolio that have 0 quantity
        if (portfolioTableOperationService.getPortfolioQuantity(portfolioId) <= 0){
            portfolioTableOperationService.removeStockFromPortfolioByPortfolioId(portfolioId);
        }

        // Deduct the stock quantity from the seller's available quantity in the Orders table
        ordersTableOperationService.updateOrderQuantity(orderId, sellerQuantity - quantity);

        //Checks and remove if that item has 0 quantity
        if (ordersTableOperationService.getOrderQuantity(orderId) <= 0){
            ordersTableOperationService.removeOrderByOrderId(orderId);
        }

        // Deduct the stock quantity * price from the seller's funds in the Users table
        double sellerBalance = userService.getUserFunds(sellerId);
        userService.updateUserFunds(sellerId, sellerBalance + (quantity * 100 * sellerPrice)); // 1 lot = 100 shares

        // Insert the trade details into the Transactions table for the seller
        transactionsTableOperationService.insertTransaction(sellerId, stockSymbol, price, quantity, "SELL");
    }

    private void executePartialTrade(String stockSymbol, double price, int quantity, int sellerId, double sellerPrice,
                                     int sellerQuantity, int orderId) throws SQLException {
        // Execute a partial trade when the matched selling order has a lesser quantity than the desired quantity
        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity, price);
        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol, sellerQuantity);
        PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingPortfolioItem(sellerId, stockSymbol);
        
        int portfolioId = portfolioitem.getPortfolioId();
        portfolioTableOperationService.updateStockQuantity(portfolioId, stockSymbol, 0);

        //Checks and remove the items in the portfolio that have 0 quantity
        if (portfolioTableOperationService.getPortfolioQuantity(portfolioId) <= 0){
            portfolioTableOperationService.removeStockFromPortfolioByPortfolioId(portfolioId);
        }

        // Deduct the stock quantity from the seller's available quantity in the Orders table
        ordersTableOperationService.updateOrderQuantity(orderId, 0);

        //Checks and remove if that item has 0 quantity
        if (ordersTableOperationService.getOrderQuantity(orderId) <= 0){
            ordersTableOperationService.removeOrderByOrderId(orderId);
        }

        // Deduct the stock quantity * price from the buyer's funds in the Users table
        double sellerBalance = userService.getUserFunds(sellerId);
        userService.updateUserFunds(sellerId, sellerBalance + (sellerQuantity * 100 * sellerPrice)); // 1 lot = 100 shares

        // Add the stock to the buyer's portfolio in the Portfolio table with the purchased quantity
//        portfolioTableOperationService.addStockToPortfolio(sellerId, stockSymbol, sellerQuantity, price);
        portfolioTableOperationService.addOrUpdateStock(sellerId, stockSymbol, sellerQuantity, price);

        // Insert the trade details into the Transactions table
        transactionsTableOperationService.insertTransaction(sellerId, stockSymbol, price, sellerQuantity, "SELL");


    }

    public void sellStock(String stockSymbol, double desiredPrice, int desiredQuantity, int sellerId) throws SQLException {
        // Step 1: Check if the seller owns enough stock in the portfolio
        int portfolioQuantity = portfolioTableOperationService.getTotalStockQuantity(sellerId, stockSymbol);
        if (portfolioQuantity < desiredQuantity){
            throw new IllegalArgumentException("Seller does not own enough stock to sell.");
        }

        // Step 2: Check if the desired price is within 10% of the current price
        Stock stock = stockTableOperationService.getStock(stockSymbol);
        double currentPrice = stock.getPrice();
        double priceThreshold = currentPrice * 0.1;

        if (Math.abs(desiredPrice - currentPrice) > priceThreshold) {
            throw new IllegalArgumentException("Desired price is not within 10% of the current price.");
        }

        // Step 4: Check if there is a matching order in the Order table
        List<Order> matchingOrders = ordersTableOperationService.getMatchingBuyOrders(stockSymbol, desiredPrice, sellerId);

        int leftoverStockQuantity = desiredQuantity;
        int totalQuantityInMatchingOrders = 0;
        boolean isMatchOrdersEmpty = matchingOrders.isEmpty();

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
                    // PortfolioItem firstPortfolio = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                    // PortfolioItem firstPortfolio = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity);
                    PortfolioItem firstPortfolio = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol);
                    if (firstPortfolio == null) {
                        break;
                    }
                    int firstPortfolioQuantity = firstPortfolio.getQuantity();

                    // Step 5.1: Check if the buyer's order quantity is greater than or equal to the seller's portfolio quantity
                    if (buyerQuantity >= firstPortfolioQuantity) {
                        // Step 5.1: Execute the full trade
                        //This method will affect the buyer's side
                        executeSellTrade(stockSymbol, desiredPrice, firstPortfolioQuantity, buyerId, buyerPrice,
                                buyerQuantity, orderId);

                        //These methods will affect the seller's side
                        // Update seller's funds
                        userService.updateUserFunds(sellerId, (userService.getUserFunds(sellerId)) + (firstPortfolioQuantity * 100 * desiredPrice)); // 1 lot = 100 shares

                        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
                        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity);
                        PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol);
                        
                        int portfolioId = portfolioitem.getPortfolioId();
                        portfolioTableOperationService.updateStockQuantity(portfolioId, stockSymbol, 0);

                        //Checks and remove the items in the portfolio that have 0 quantity
                        if (portfolioTableOperationService.getPortfolioQuantity(portfolioId) <= 0) {
                            portfolioTableOperationService.removeStockFromPortfolioByPortfolioId(portfolioId);
                        }

                        // Add transaction for the seller
                        transactionsTableOperationService.insertTransaction(sellerId, stockSymbol, desiredPrice, firstPortfolioQuantity, "SELL");

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
                        userService.updateUserFunds(sellerId, (userService.getUserFunds(sellerId)) + (buyerQuantity * 100 * desiredPrice)); // 1 lot = 100 shares

                        // Deduct the stock quantity from the seller's portfolio in the Portfolio table
                        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity, desiredPrice);
                        // PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol, desiredQuantity);
                        PortfolioItem portfolioitem = portfolioTableOperationService.getEarliestMatchingSellPortfolioItem(sellerId, stockSymbol);
                        int portfolioId = portfolioitem.getPortfolioId();
                        portfolioTableOperationService.updateStockQuantity(portfolioId, stockSymbol, (firstPortfolioQuantity - buyerQuantity));

                        //Checks and remove the items in the portfolio that have 0 quantity
                        if (portfolioTableOperationService.getPortfolioQuantity(portfolioId) <= 0) {
                            portfolioTableOperationService.removeStockFromPortfolioByPortfolioId(portfolioId);
                        }

                        // Add transaction for the seller
                        transactionsTableOperationService.insertTransaction(sellerId, stockSymbol, desiredPrice, buyerQuantity, "SELL");

                        //If the desired quantity still have leftover, then the leftoverStockQuantity will keep decreasing until 0 for each trade
                        leftoverStockQuantity = leftoverStockQuantity - buyerQuantity;
                        buyerQuantity = 0;

                    }
                }
            }
        } else {
            // Step 4.5: Place a new "SELL" order in the Orders table
            ordersTableOperationService.insertOrder(sellerId, stockSymbol, "SELL", leftoverStockQuantity, desiredPrice);
            throw new IllegalArgumentException("There are no matching orders. A new order has been placed.");
        }

        //If there are still leftover quantity to be sold, then the remaining will be placed as an Order
        if (leftoverStockQuantity > 0 && (isMatchOrdersEmpty == false)){
            ordersTableOperationService.insertOrder(sellerId, stockSymbol, "SELL", leftoverStockQuantity, desiredPrice);
            throw new IllegalArgumentException("There are still leftover quantity to be sold. A new order has been placed.");
        }

    }

    //Only used in selling stock to update the buyer's side of the trade when the quantity in the buyer's order is bigger than the seller's first portfolio amount
    private void executeSellTrade(String stockSymbol, double tradePrice, int tradeQuantity, int buyerId, double buyerPrice,
                             int buyerQuantity, int orderId) throws SQLException {
        // Calculate the total trade value
        double totalTradeValue = tradePrice * tradeQuantity * 100; // 100 shares per lot

        // Update buyer's funds
        double buyerFunds = userService.getUserFunds(buyerId);
        double updatedBuyerFunds = buyerFunds - totalTradeValue; 
        userService.updateUserFunds(buyerId, updatedBuyerFunds);

        // Deduct the stock quantity from the buyer's available quantity in the Orders table
        ordersTableOperationService.updateOrderQuantity(orderId, buyerQuantity - tradeQuantity);

        //Checks and remove if that item has 0 quantity in Orders table
        if (ordersTableOperationService.getOrderQuantity(orderId) <= 0){
            ordersTableOperationService.removeOrderByOrderId(orderId);
        }

        // Add stock to buyer's portfolio
        // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, tradeQuantity, tradePrice);
        portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, tradeQuantity, tradePrice);

        // Add transaction for the buyer
        transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, tradePrice, tradeQuantity, "BUY");

    }

    //Only used in selling stock to update the buyer's side of the trade when the quantity in the buyer's order is smaller than the seller's first portfolio amount
    private void executePartialSellTrade(String stockSymbol, double tradePrice, int tradeQuantity, int buyerId, double buyerPrice,
                                    int buyerQuantity, int orderId) throws SQLException {
        // Calculate the total trade value
        double totalTradeValue = tradePrice * buyerQuantity * 100; // 100 shares per lot

        // Update seller's funds
        double buyerFunds = userService.getUserFunds(buyerId);
        double updatedBuyerFunds = buyerFunds - totalTradeValue;
        userService.updateUserFunds(buyerId, updatedBuyerFunds);

        // Deduct the stock quantity from the buyer's available quantity in the Orders table
        ordersTableOperationService.updateOrderQuantity(orderId,  0);

        //Checks and remove if that item has 0 quantity in Orders table
        if (ordersTableOperationService.getOrderQuantity(orderId) <= 0){
            ordersTableOperationService.removeOrderByOrderId(orderId);
        }

        // Add stock to buyer's portfolio
        // portfolioTableOperationService.addStockToPortfolio(buyerId, stockSymbol, buyerQuantity, tradePrice);
        portfolioTableOperationService.addOrUpdateStock(buyerId, stockSymbol, buyerQuantity, tradePrice);

        // Add transaction for the buyer
        transactionsTableOperationService.insertTransaction(buyerId, stockSymbol, tradePrice, buyerQuantity, "BUY");
    }
}
