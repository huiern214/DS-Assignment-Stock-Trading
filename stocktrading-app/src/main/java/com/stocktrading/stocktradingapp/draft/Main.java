/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

// package com.mycompany.main;
package com.stocktrading.stocktradingapp.draft;

/**
 *
 * @author abdullahiibrahim
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Trade {
    private String symbol;
    private int quantity;
    private double entryPrice;
    private double exitPrice;
    private String entryTime;
    private String exitTime;

    public Trade(String symbol, int quantity, double entryPrice, double exitPrice, String entryTime, String exitTime) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    // Getters and Setters

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }
}

class TradingDashboard {
    private double accountBalance;
    private List<Trade> openPositions;
    private List<Trade> tradeHistory;

    public TradingDashboard(double accountBalance) {
        this.accountBalance = accountBalance;
        this.openPositions = new ArrayList<>();
        this.tradeHistory = new ArrayList<>();
    }

    public void addPosition(String symbol, int quantity, double entryPrice) {
        Trade position = new Trade(symbol, quantity, entryPrice, 0, "", "");
        openPositions.add(position);
    }

    public void closePosition(String symbol, double exitPrice) {
        for (Trade position : openPositions) {
            if (position.getSymbol().equals(symbol) && position.getExitPrice() == 0) {
                position.setExitPrice(exitPrice);
                position.setExitTime("Some Exit Time"); // Set the exit time according to your requirement
                tradeHistory.add(position);
                openPositions.remove(position);
                break;
            }
        }
    }

    public double calculatePoints(Trade trade) {
        double pnl = (trade.getExitPrice() - trade.getEntryPrice()) * trade.getQuantity();
        return (pnl / accountBalance) * 100;
    }

    public void displayAccountBalance() {
        System.out.println("Account Balance: $" + accountBalance);
    }

    public void displayCurrentPnl() {
        double totalPnl = 0;
        for (Trade trade : tradeHistory) {
            totalPnl += (trade.getExitPrice() - trade.getEntryPrice()) * trade.getQuantity();
        }
        System.out.println("Current P&L: $" + totalPnl);
    }

    public void displayPoints() {
        double totalPoints = 0;
        for (Trade trade : tradeHistory) {
            totalPoints += calculatePoints(trade);
        }
        System.out.println("Points: " + totalPoints);
    }

    public void displayOpenPositions() {
        System.out.println("Open Positions:");
        for (Trade position : openPositions) {
            System.out.println("Symbol: " + position.getSymbol() +
                    ", Quantity: " + position.getQuantity() +
                    ", Entry Price: $" + position.getEntryPrice());
        }
    }

    public void displayTradeHistory() {
        System.out.println("Trade History:");
        System.out.println("Symbol\tQuantity\tEntry Price\tExit Price\tEntry Time\tExit Time");
        for (Trade trade : tradeHistory) {
            System.out.println(trade.getSymbol() + "\t" +
                    trade.getQuantity() + "\t\t$" +
                    trade.getEntryPrice() + "\t\t$" +
                    trade.getExitPrice() + "\t\t" +
                    trade.getEntryTime() + "\t" +
                    trade.getExitTime());
        }
    }

    public void sortTradeHistoryByPrice() {
        Collections.sort(tradeHistory, Comparator.comparing(Trade::getEntryPrice));
    }

    public void sortTradeHistoryByTime() {
        Collections.sort(tradeHistory, Comparator.comparing(Trade::getEntryTime));
    }
}

public class Main {
    public static void main(String[] args) {
        TradingDashboard dashboard = new TradingDashboard(10000.0);
        dashboard.addPosition("AAPL", 10, 150.0);
        dashboard.addPosition("GOOG", 5, 2000.0);
        dashboard.closePosition("AAPL", 155.0);
        dashboard.closePosition("GOOG", 2001.0);

        dashboard.displayAccountBalance();
        dashboard.displayCurrentPnl();
        dashboard.displayPoints();
        dashboard.displayOpenPositions();
        dashboard.displayTradeHistory();

        System.out.println("\nSorting Trade History by Price:");
        dashboard.sortTradeHistoryByPrice();
        dashboard.displayTradeHistory();

        System.out.println("\nSorting Trade History by Time:");
        dashboard.sortTradeHistoryByTime();
        dashboard.displayTradeHistory();
    }
}

