package com.stocktrading.stocktradingapp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Leaderboard;
import com.stocktrading.stocktradingapp.model.User;

@Service
public class LeaderboardService {
    private final TradingDashboardService tradingDashboardService;
    private final UserService userService;

    public LeaderboardService(TradingDashboardService tradingDashboardService, UserService userService) {
        this.tradingDashboardService = tradingDashboardService;
        this.userService = userService;
    }

    public List<Leaderboard> getTop10Leaderboard() throws SQLException {
        List<User> users = userService.getAllUsers();
    
        // Calculate total points for each user
        List<Leaderboard> leaderboardList = new ArrayList<>();
        for (User user : users) {
            double totalPoints = tradingDashboardService.getPoints(user.getUserId());
            leaderboardList.add(new Leaderboard(user.getUserId(), user.getUsername(), totalPoints));
        }
    
        // Sort the leaderboard based on total points
        leaderboardList.sort(Comparator.comparingDouble(Leaderboard::getTotalPoints).reversed());
    
        // Get the top 10 leaderboard entries
        List<Leaderboard> top10Leaderboard = new ArrayList<>();
        int count = Math.min(leaderboardList.size(), 10);
        for (int i = 0; i < count; i++) {
            top10Leaderboard.add(leaderboardList.get(i));
        }
    
        return top10Leaderboard;
    }    
}
