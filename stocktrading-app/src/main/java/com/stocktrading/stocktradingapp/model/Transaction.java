package com.stocktrading.stocktradingapp.model;

public class Transaction {
    private int transactionId;
    private int userId;
    private String stockSymbol;
    private String transactionType;
    private int quantity;
    private double price;
    private String timestamp;

    public Transaction(int transactionId, int userId, String stockSymbol, String transactionType, int quantity, double price, String timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public void printTransactionInfo() {
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("User ID: " + userId);
        System.out.println("Stock Symbol: " + stockSymbol);
        System.out.println("Transaction Type: " + transactionType);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);
        System.out.println("Timestamp: " + timestamp);
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }
    public int getUserId() {
        return userId;
    }
    public String getStockSymbol() {
        return stockSymbol;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
