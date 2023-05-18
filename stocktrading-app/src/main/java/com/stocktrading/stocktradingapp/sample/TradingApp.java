package com.stocktrading.stocktradingapp.sample;

import java.util.List;

public class TradingApp {
    private List<User> users;
    private TradingEngine tradingEngine;

    public TradingApp(List<User> users, TradingEngine tradingEngine) {
        this.users = users;
        this.tradingEngine = tradingEngine;
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void placeOrder(User user, Order order) {
        tradingEngine.executeOrder(order, user.getPortfolio());
    }
}

