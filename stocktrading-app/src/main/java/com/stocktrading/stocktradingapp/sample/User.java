package com.stocktrading.stocktradingapp.sample;

public class User {
    private String name;
    private String email;
    private String password;
    private Portfolio portfolio;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.portfolio = new Portfolio();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}

