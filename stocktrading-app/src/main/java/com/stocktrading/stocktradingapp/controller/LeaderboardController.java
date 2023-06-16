package com.stocktrading.stocktradingapp.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stocktrading.stocktradingapp.model.Leaderboard;
import com.stocktrading.stocktradingapp.service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // http://localhost:8080/api/leaderboard/top10
    @GetMapping("/top10")
    public ResponseEntity<List<Leaderboard>> getTop10Leaderboard() {
        try {
            List<Leaderboard> top10Leaderboard = leaderboardService.getTop10Leaderboard();
            return ResponseEntity.ok(top10Leaderboard);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Example output:
    // [{"userId":2,"username":"Lily","totalPoints":0.0},{"userId":1,"username":"Ali","totalPoints":-0.021999999999999888}]

    // http://localhost:8080/api/leaderboard/1/rank
    @GetMapping("/{userId}/rank")
    public ResponseEntity<Integer> getCurrentRank(@PathVariable int userId) {
        try {
            int currentRank = leaderboardService.getCurrentRank(userId);
            if (currentRank != -1) {
                return ResponseEntity.ok(currentRank);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // 4
}

