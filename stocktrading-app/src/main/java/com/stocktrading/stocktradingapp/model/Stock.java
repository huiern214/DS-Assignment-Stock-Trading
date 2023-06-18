package com.stocktrading.stocktradingapp.model;

import java.util.Objects;

public class Stock implements Comparable<Stock>{
    private String symbol;
    private String name; // company name
    private double price; // market price
    private double priceChange; // for display purposes (in 4 decimal places)
    private double priceChangePercent; // for display purposes (in 4 decimal places)
    private int systemQuantity; // for system use


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

    public Stock(String symbol, String name, double price, double priceChange, double priceChangePercent, int systemQuantity) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.priceChange = priceChange;
        this.priceChangePercent = priceChangePercent;
        this.systemQuantity = systemQuantity;
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

    public int getSystemQuantity() {
        return systemQuantity;
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

    public void setSystemQuantity(int systemQuantity) {
        this.systemQuantity = systemQuantity;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}


