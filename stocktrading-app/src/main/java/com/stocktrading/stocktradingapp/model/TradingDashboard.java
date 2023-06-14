package com.stocktrading.stocktradingapp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TradingDashboard {
    private final double startingAccBalance = 50000;
    private List<Trade> openPositions;
    private List<Trade> tradeHistory;
    private double totalPnL;

    public TradingDashboard() {
        this.openPositions = new ArrayList<>();
        this.tradeHistory = new ArrayList<>();
        this.totalPnL = 0;
    }

    // Getters and Setters
    public List<Trade> getOpenPositions() {
        return openPositions;
    }
    public void setOpenPositions(List<Trade> openPositions) {
        this.openPositions = openPositions;
    }

    public List<Trade> getTradeHistory() {
        return tradeHistory;
    }

    // Methods
    // Add a new position to the list of open positions
    public void addPosition(String symbol, int quantity, double entryPrice, String entryTime, double stockCurrentPrice) {
        Trade position = new Trade(symbol, quantity, entryPrice, entryTime, stockCurrentPrice, "");
        openPositions.add(position);
    }

    // Close a position by setting the exit price and exit time
    public void closePosition(String symbol, double exitPrice, String exitTime) {
        for (Trade position : openPositions) {
            if (position.getSymbol().equals(symbol)) {
                position.setExitPrice(exitPrice);
                position.setExitTime(exitTime);
                tradeHistory.add(position);
                openPositions.remove(position);
                totalPnL += position.getPnL();
                break;
            }
        }
    }

    // Get the current realised PnL
    public double getTotalPnL() {
        return totalPnL;
    }

    // Get the current realised PnL in percentage
    public double getTotalPnLPercentage() {
        double investedAmount = 0;
        for (Trade trade : tradeHistory) {
            investedAmount += (trade.getEntryPrice() * trade.getqtySold() * 100);
        }
        return totalPnL / investedAmount * 100;
    }

    // Get the total points gained or lost by the user
    public double getTotalPoints() {
        return getTotalPnL() / startingAccBalance * 100;
    }

    // unrealised PnL = (current price - entry price) * quantity
    // Get the current unrealised PnL by summing up the PnL of all the trades in the open positions
    public double getUnrealisedPnL() {
        double totalUnrealisedPnL = 0;
        for (Trade position : openPositions) {
            totalUnrealisedPnL += position.getPnL();
        }
        return totalUnrealisedPnL;
    }

    // Get the current unrealised PnL in percentage
    public double getUnrealisedPnLPercentage() {
        double investedAmount = 0;
        for (Trade trade : openPositions) {
            investedAmount += (trade.getEntryPrice() * trade.getqtySold() * 100);
        }
        return getUnrealisedPnL() / investedAmount * 100;
    }

    // Get the open positions of the user by symbol
    public List<Trade> getOpenPositionsBySymbol(String symbol) {
        List<Trade> positions = new ArrayList<>();
        for (Trade position : openPositions) {
            if (position.getSymbol().equals(symbol)) {
                positions.add(position);
            }
        }
        return positions;
    }

    // Get the trade history of the user by symbol
    public List<Trade> getTradeHistoryBySymbol(String symbol) {
        List<Trade> trades = new ArrayList<>();
        for (Trade trade : tradeHistory) {
            if (trade.getSymbol().equals(symbol)) {
                trades.add(trade);
            }
        }
        return trades;
    }

    // Sort the trade history by symbol, price, or time
    public void sortTradeHistoryBySymbol() {
        Collections.sort(tradeHistory, Comparator.comparing(Trade::getSymbol));
    }
    public void sortTradeHistoryByPrice() {
        Collections.sort(tradeHistory, Comparator.comparing(Trade::getEntryPrice));
    }
    public void sortTradeHistoryByTime() {
        Collections.sort(tradeHistory, Comparator.comparing(Trade::getEntryTime));
    }

    // Display the open positions and trade history
    public void displayOpenPositions() {
        System.out.println("Open Positions:");
        for (Trade position : openPositions) {
            System.out.println("Symbol: " + position.getSymbol() +
                    ", Quantity: " + position.getqtySold() +
                    ", Entry Price: $" + position.getEntryPrice());
        }
    }

    public void displayTradeHistory() {
        System.out.println("Trade History:");
        System.out.println("Symbol\tQuantity\tEntry Price\tExit Price\tEntry Time\tExit Time");
        for (Trade trade : tradeHistory) {
            System.out.println(trade.getSymbol() + "\t" +
                    trade.getqtySold() + "\t\t$" +
                    trade.getEntryPrice() + "\t\t$" +
                    trade.getExitPrice() + "\t\t" +
                    trade.getEntryTime() + "\t" +
                    trade.getExitTime());
        }
    }
}
