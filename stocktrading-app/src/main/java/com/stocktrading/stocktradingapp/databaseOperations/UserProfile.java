package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int userId;
    private String username;
    private String email;
    private List<PortfolioItem> portfolio;

    private double funds;

    public UserProfile(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.portfolio = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<PortfolioItem> getPortfolio() {
        return portfolio;
    }

    public double getFunds() {return funds;}

    public void addPortfolioItem(PortfolioItem portfolioItem) {
        portfolio.add(portfolioItem);
    }

    public void setFunds(Connection connection, double funds) throws SQLException {

        String setFundsQuery = "UPDATE Users SET funds = ? WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(setFundsQuery)) {
            statement.setDouble(1, funds);
            statement.setInt(2, userId);

            statement.executeUpdate();
        }

        this.funds = funds; // Update the funds field in the UserProfile object
    }
}
