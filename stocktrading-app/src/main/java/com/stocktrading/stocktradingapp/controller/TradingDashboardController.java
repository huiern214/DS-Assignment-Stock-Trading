package com.stocktrading.stocktradingapp.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stocktrading.stocktradingapp.model.Portfolio;
import com.stocktrading.stocktradingapp.model.Trade;
import com.stocktrading.stocktradingapp.model.TradingDashboard;
import com.stocktrading.stocktradingapp.service.TradingDashboardService;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class TradingDashboardController {
    private final TradingDashboardService tradingDashboardService;

    @Autowired
    public TradingDashboardController(TradingDashboardService tradingDashboardService) {
        this.tradingDashboardService = tradingDashboardService;
    }

    // http://localhost:8080/dashboard/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<TradingDashboard> getTradingDashboardByUserId(@PathVariable int userId) {
        try {
            TradingDashboard tradingDashboard = tradingDashboardService.getTradingHistoryByUserId(userId);
            return ResponseEntity.ok(tradingDashboard);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // {"openPositions":[{"symbol":"1015.KL","qtySold":5,"entryPrice":2.6,"exitPrice":3.56,"entryTime":"2023-06-12 07:53:55","exitTime":"","pnL":480.0,"roi":36.92307692307693}],
    // "tradeHistory":[{"symbol":"1023.KL","qtySold":1,"entryPrice":5.1,"exitPrice":4.99,"entryTime":"2023-06-10 18:39:32","exitTime":"2023-06-13 17:37:01","pnL":-10.999999999999943,"roi":-2.1568627450980284}],
    // "totalPnL":-10.999999999999943,"totalPoints":-0.021999999999999888,
    // "unrealisedPnL":480.0,"unrealisedPnLPercentage":36.92307692307693,"totalPnLPercentage":-2.1568627450980284}
    
    // http://localhost:8080/dashboard/{userId}/portfolio
    @GetMapping("/{userId}/portfolio")
    public ResponseEntity<Portfolio> getPortfolioByUserId(@PathVariable int userId) {
        try {
            Portfolio portfolioValue = tradingDashboardService.getPortfolio(userId);
             // Remove entries with null keys from the portfolio's holdings map
            portfolioValue.getHoldings().entrySet().removeIf(entry -> entry.getKey() == null);
            return ResponseEntity.ok(portfolioValue);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // {"holdings":{"AMMB Holdings Berhad (AMBANK)":5},"value":1775.0}

    // http://localhost:8080/dashboard/{userId}/points
    @GetMapping("/{userId}/points")
    public ResponseEntity<Double> getPointsByUserId(@PathVariable int userId) {
        try {
            double realisedPoints = tradingDashboardService.getPoints(userId);
            return ResponseEntity.ok(realisedPoints);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // -0.021999999999999888

    // http://localhost:8080/dashboard/{userId}/open-positions
    @GetMapping("/{userId}/open-positions")
    public ResponseEntity<List<Trade>> getOpenPositionsByUserId(@PathVariable int userId) {
        try {
            List<Trade> openPositions = tradingDashboardService.getOpenPositions(userId);
            return ResponseEntity.ok(openPositions);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // [{"symbol":"1015.KL","qtySold":5,"entryPrice":2.6,"exitPrice":3.55,"entryTime":"2023-06-12 07:53:55","exitTime":"","roi":36.53846153846153,"pnL":474.99999999999983}]

    // http://localhost:8080/dashboard/{userId}/trade-history
    @GetMapping("/{userId}/trade-history")
    public ResponseEntity<List<Trade>> getTradeHistoryByUserId(@PathVariable int userId) {
        try {
            List<Trade> tradeHistory = tradingDashboardService.getTradeHistory(userId);
            return ResponseEntity.ok(tradeHistory);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // [{"symbol":"1023.KL","qtySold":1,"entryPrice":5.1,"exitPrice":4.99,"entryTime":"2023-06-10 18:39:32","exitTime":"2023-06-13 17:37:01","roi":-2.1568627450980284,"pnL":-10.999999999999943}]

    // http://localhost:8080/dashboard/{userId}/trade-history/{symbol}
    @GetMapping("/{userId}/trade-history/{symbol}")
    public ResponseEntity<List<Trade>> getTradeHistoryBySymbol(@PathVariable int userId, @PathVariable String symbol) {
        try {
            List<Trade> tradeHistoryBySymbol = tradingDashboardService.getTradeHistoryBySymbol(userId, symbol);
            return ResponseEntity.ok(tradeHistoryBySymbol);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // [{"symbol":"1023.KL","qtySold":1,"entryPrice":5.1,"exitPrice":4.99,"entryTime":"2023-06-10 18:39:32","exitTime":"2023-06-13 17:37:01","roi":-2.1568627450980284,"pnL":-10.999999999999943}]
}

