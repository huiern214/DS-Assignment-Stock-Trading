package com.stocktrading.stocktradingapp.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradingEngine {
    private List<Stock> stocks;
    private Map<Stock, List<Order>> buyOrders;
    private Map<Stock, List<Order>> sellOrders;

    public TradingEngine(List<Stock> stocks) {
        this.stocks = stocks;
        this.buyOrders = new HashMap<>();
        this.sellOrders = new HashMap<>();
        for (Stock stock : stocks) {
            buyOrders.put(stock, new ArrayList<>());
            sellOrders.put(stock, new ArrayList<>());
        }
    }

    public void executeOrder(Order order, Portfolio portfolio) {
        if (order.getType() == Order.Type.BUY) {
            buyOrders.get(order.getStock()).add(order);
            tryExecuteBuyOrders(order.getStock(), portfolio);
        } else {
            sellOrders.get(order.getStock()).add(order);
            tryExecuteSellOrders(order.getStock(), portfolio);
        }
    }

    private void tryExecuteBuyOrders(Stock stock, Portfolio portfolio) {
        List<Order> orders = buyOrders.get(stock);
        double price = stock.getPrice();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getPrice() >= price) {
                int currentShares = portfolio.getHoldings().getOrDefault(stock, 0);
                double totalPrice = order.getPrice() * order.getShares();
                if (portfolio.getValue() >= totalPrice) {
                    portfolio.addStock(stock, order.getShares());
                    orders.remove(i);
                    i--;
                }
            }
        }
    }

    private void tryExecuteSellOrders(Stock stock, Portfolio portfolio) {
        List<Order> orders = sellOrders.get(stock);
        double price = stock.getPrice();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getPrice() <= price) {
                int currentShares = portfolio.getHoldings().getOrDefault(stock, 0);
                if (currentShares >= order.getShares()) {
                    portfolio.removeStock(stock, order.getShares());
                    orders.remove(i);
                    i--;
                }
            }
        }
    }

    public void updatePrices() {
        for (Stock stock : stocks) {
            // Update the stock price based on some market data source
            double newPrice = 1; // Get the new price from some market data source
            stock.setPrice(newPrice);
            tryExecuteBuyOrders(stock, new Portfolio());
            tryExecuteSellOrders(stock, new Portfolio());
        }
    }
}

