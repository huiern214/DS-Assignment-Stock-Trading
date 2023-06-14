package com.stocktrading.stocktradingapp.model;

public class Trade {
    private String symbol;
    private int qtySold; // number of lots sold (1 lot = 100 shares)
    private double entryPrice;
    private double exitPrice;
    private String entryTime;
    private String exitTime;

    public Trade(String symbol, int qtySold, double entryPrice, String entryTime, double exitPrice, String exitTime) {
        this.symbol = symbol;
        this.qtySold = qtySold;
        this.entryPrice = entryPrice;
        this.entryTime = entryTime;
        this.exitPrice = exitPrice;
        this.exitTime = exitTime;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getqtySold() {
        return qtySold;
    }
    public void setqtySold(int qtySold) {
        this.qtySold = qtySold;
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

    // Methods
    // Calculate the profit or loss of the trade (realized PnL)
    public double getPnL() {
        return (exitPrice - entryPrice) * qtySold * 100;
    }
    
    // Calculate the return on investment (ROI) of the trade
    public double getROI() {
        double totalInvestment = entryPrice * (qtySold * 100); // Calculate the total investment (entry price multiplied by the total number of shares)
        double profit = (exitPrice * (qtySold * 100)) - totalInvestment; // Calculate the profit (exit price multiplied by the total number of shares minus the total investment)
        return (profit / totalInvestment) * 100; // Calculate the ROI
    }
}


