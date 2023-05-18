package com.stocktrading.stocktradingapp.sample;

public class Order {
    public enum Type {
        BUY,
        SELL
    }

    private Stock stock;
    private Type type;
    private int shares;
    private double price;

    public Order(Stock stock, Type type, int shares, double price) {
        this.stock = stock;
        this.type = type;
        this.shares = shares;
        this.price = price;
    }

    public Stock getStock() {
        return stock;
    }

    public Type getType() {
        return type;
    }

    public int getShares() {
        return shares;
    }

    public double getPrice() {
        return price;
    }
}

