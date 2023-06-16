package com.stocktrading.stocktradingapp.model;

public class Stock implements Comparable<Stock>{
    private String symbol;
    private String name; // company name
    private double price; // market price
    private double priceChange; // for display purposes (in 4 decimal places)
    private double priceChangePercent; // for display purposes (in 4 decimal places)


    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public Stock(String symbol, String name, double price, double priceChange, double priceChangePercent) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.priceChange = priceChange;
        this.priceChangePercent = priceChangePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public double getPriceChangePercent() {
        return priceChangePercent;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public void setPriceChangePercent(double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }

    @Override
    public String toString() {
        return "["+symbol + "] " + name;
    }

    @Override
    public int compareTo(Stock other) {
        // Implement the comparison logic based on the company name
        return this.getName().compareTo(other.getName());
    }
}


