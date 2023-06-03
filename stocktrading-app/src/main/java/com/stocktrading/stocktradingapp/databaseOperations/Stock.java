package com.stocktrading.stocktradingapp.databaseOperations;

public class Stock {
    // private int stockId;
    private String companyName;
    private String stockSymbol;
    private double currentPrice;

    public Stock(String companyName, String stockSymbol, double currentPrice) {
        // this.stockId = stockId;
        this.companyName = companyName;
        this.stockSymbol = stockSymbol;
        this.currentPrice = currentPrice;
    }

    // public int getStockId() {
    // return stockId;
    // }

    public String getCompanyName() {
        return companyName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }
}