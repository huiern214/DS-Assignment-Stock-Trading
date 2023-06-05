package com.stocktrading.stocktradingapp.model;

public class UserProfile {
    private String username;
    private String email;
    private Double funds;

    public UserProfile(String username, String email, Double funds) {
        this.username = username;
        this.email = email;
        this.funds = funds;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }
}
