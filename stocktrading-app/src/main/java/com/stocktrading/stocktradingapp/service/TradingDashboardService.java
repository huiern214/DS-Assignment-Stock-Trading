package com.stocktrading.stocktradingapp.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Portfolio;
import com.stocktrading.stocktradingapp.model.Trade;
import com.stocktrading.stocktradingapp.model.TradingDashboard;
import com.stocktrading.stocktradingapp.model.Transaction;
import com.stocktrading.stocktradingapp.model.User;
import com.stocktrading.stocktradingapp.service.databaseOperations.StockTableOperationService;
import com.stocktrading.stocktradingapp.service.databaseOperations.TransactionsTableOperationService;

@Service
public class TradingDashboardService {
    private final TransactionsTableOperationService transactionsTableOperationService;
    private final StockTableOperationService stockTableOperationService;
    private final UserService userService;
   
    public TradingDashboardService(TransactionsTableOperationService transactionsTableOperationService, StockTableOperationService stockTableOperationService, UserService userService) {
        this.transactionsTableOperationService = transactionsTableOperationService;
        this.stockTableOperationService = stockTableOperationService;
        this.userService = userService;
    }
   
    // Get all transactions by userId and return a TradingDashboard object
    public TradingDashboard getTradingHistoryByUserId(int userId) throws SQLException {
        List<Transaction> transactions = transactionsTableOperationService.getTransactionsByUser(userId);

        if (transactions.size() == 0) {
            return new TradingDashboard();
        }

        TradingDashboard tradingDashboard = new TradingDashboard();

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType().equals("BUY")) {
                double stockCurrentPrice = stockTableOperationService.getStock(transaction.getStockSymbol()).getPrice();
                tradingDashboard.addPosition(transaction.getStockSymbol(), transaction.getQuantity(), transaction.getPrice(), transaction.getTimestamp(), stockCurrentPrice);
            } else if (transaction.getTransactionType().equals("SELL")) {
                // Check if there are open positions with the same stock symbol
                List<Trade> openPositions = tradingDashboard.getOpenPositionsBySymbol(transaction.getStockSymbol());

                for (Trade openPosition : openPositions) {
                    if (openPosition.getqtySold() > transaction.getQuantity()) {
                        // If the open position has more quantity than the sell transaction,
                        // split the open position and create a new open position with the remaining quantity
                        int remainingQty = openPosition.getqtySold() - transaction.getQuantity();
                        openPosition.setqtySold(remainingQty);
                        tradingDashboard.addPosition(openPosition.getSymbol(), transaction.getQuantity(), openPosition.getEntryPrice(), openPosition.getEntryTime(), openPosition.getExitPrice());
                        break;
                    } else if (openPosition.getqtySold() == transaction.getQuantity()) {
                        // If the open position has the same quantity as the sell transaction,
                        // close the position
                        tradingDashboard.closePosition(openPosition.getSymbol(), transaction.getPrice(), transaction.getTimestamp());
                        break;
                    } else {
                        // If the open position has less quantity than the sell transaction,
                        // close the position and reduce the remaining quantity from the sell transaction
                        transaction.setQuantity(transaction.getQuantity() - openPosition.getqtySold());
                        tradingDashboard.closePosition(openPosition.getSymbol(), transaction.getPrice(), transaction.getTimestamp());
                    }
                }
            }
        }
        return tradingDashboard;
    }

    // Get other information from TradingDashboard object
    public double getRealisedPnL(int userId) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getTotalPnL();
    }
    public double getPoints(int userId) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getTotalPoints();
    }
    public List<Trade> getOpenPositions(int userId) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getOpenPositions();
    }
    public List<Trade> getTradeHistory(int userId) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getTradeHistory();
    }
    public List<Trade> getTradeHistoryBySymbol(int userId, String symbol) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getTradeHistoryBySymbol(symbol);
    }
    public double getUnrealisedPnL(int userId) throws SQLException {
        TradingDashboard tradingDashboard = getTradingHistoryByUserId(userId);
        return tradingDashboard.getUnrealisedPnL();
    }

    // Get portfolio by userId
    public Portfolio getPortfolio(int userId) throws SQLException {
        User user = userService.getUser(userId);
        return user.getPortfolio();
    }
}