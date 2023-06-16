package com.stocktrading.stocktradingapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Leaderboard {
    private int userId;
    private String username;
    private double totalPoints;

    public Leaderboard(int userId, String username, double totalPoints) {
        this.userId = userId;
        this.username = username;
        this.totalPoints = totalPoints;
    }
}
