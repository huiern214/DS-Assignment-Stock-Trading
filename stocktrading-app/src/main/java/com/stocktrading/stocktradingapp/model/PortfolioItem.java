package com.stocktrading.stocktradingapp.model;

public class PortfolioItem {
    private int portfolioId;
    private Stock stock;
    private int quantity;
    private double purchasePrice;

    public PortfolioItem( int portfolioId,Stock stock, int quantity, double purchasePrice) {
        this.stock = stock;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.portfolioId = portfolioId;
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

    public int getPortfolioId() { 
        return portfolioId; 
    }
}
