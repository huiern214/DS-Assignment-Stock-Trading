package com.stocktrading.stocktradingapp.model;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private Map<Stock, Integer> holdings;

    public Portfolio() {
        holdings = new HashMap<>();
    }

    // qty is number of lots sold (1 lot = 100 shares)
    public void addStock(Stock stock, int qty) {
        int currentQty = holdings.getOrDefault(stock, 0);
        holdings.put(stock, currentQty + qty);
    }

    public void removeStock(Stock stock, int qty) {
        int currentQty = holdings.getOrDefault(stock, 0);
        if (currentQty >= qty) {
            holdings.put(stock, currentQty - qty);
        }
    }

    public Map<Stock, Integer> getHoldings() {
        Map<Stock, Integer> holdingsWithZero = new HashMap<>();
        for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
            Stock stock = entry.getKey();
            Integer qty = entry.getValue();
            int adjustedQty = qty != null ? qty : 0;
            holdingsWithZero.put(stock, adjustedQty);
        }
        return holdingsWithZero;
    }

    public double getValue() {
        double value = 0.0;
        for (Map.Entry<Stock, Integer> entry : holdings.entrySet()) {
            Stock stock = entry.getKey();
            int qty = entry.getValue();
            value += stock.getPrice() * qty * 100;
        }
        return value;
    }

    public void setHoldings(Map<Stock, Integer> holdings2) {
    }
}

