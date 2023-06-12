package com.stocktrading.stocktradingapp.model;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private double funds;
    private Portfolio portfolio;

    public User(String username, String email, String password) {
        this.userId = -1;
        this.username = username;
        this.email = email;
        this.password = password;
        this.funds = -1;
        this.portfolio = new Portfolio();
    }

    public int getUserId(){
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getFunds() {
        return funds;
    }

    public Portfolio getPortfolio(){
        return portfolio;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }

    public void setPortfolio(Portfolio portfolio){
        this.portfolio = portfolio;
    }
}
