package com.stocktrading.stocktradingapp.databaseOperations;

public class PortfolioItem {
    private Stock stock;
    private int quantity;
    private double purchasePrice;

    public PortfolioItem(Stock stock, int quantity, double purchasePrice) {
        this.stock = stock;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }
}
