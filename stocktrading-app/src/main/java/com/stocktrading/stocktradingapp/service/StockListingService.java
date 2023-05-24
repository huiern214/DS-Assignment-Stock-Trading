package com.stocktrading.stocktradingapp.service;

import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockListingService {

    private static final long REFRESH_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static final String[] COMPANY_CODES = {"1066", "1295", "5296", "4707", "6012"};

    private final StockService stockService;
    private final PriorityQueue<Stock> stockQueue;

    private LocalDateTime lastUpdateTime;

    public StockListingService(StockService stockService) {
        this.stockService = stockService;
        this.stockQueue = new PriorityQueue<>();
        this.lastUpdateTime = LocalDateTime.now(); // Set the initial value

        initializeStockQueue();
        startRefreshing();
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public PriorityQueue<Stock> getStockQueue() {
        return stockQueue;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    private void initializeStockQueue() {
        for (String code : COMPANY_CODES) {
            String symbol = code + ".KL";
            Stock stock = stockService.getStockData(symbol);
            if (stock != null) {
                stockQueue.offer(stock);
            }
        }
    }

    private void startRefreshing() {
        TimerTask refreshTask = new TimerTask() {
            @Override
            public void run() {
                refreshStockData();
            }
        };

        Timer timer = new Timer();
        timer.schedule(refreshTask, REFRESH_INTERVAL, REFRESH_INTERVAL);
    }

    private void refreshStockData() {
        // lastUpdateTime = LocalDateTime.now();
        setLastUpdateTime(LocalDateTime.now());
        for (Stock stock : stockQueue) {
            String symbol = stock.getSymbol();
            Stock updatedStock = stockService.getStockData(symbol);
            if (updatedStock != null) {
                stock.setPrice(updatedStock.getPrice());
                stock.setPriceChange(updatedStock.getPriceChange());
                stock.setPriceChangePercent(updatedStock.getPriceChangePercent());
            }
        }
    }

    // Other methods for accessing and manipulating the stockQueue as needed
    public void addStock(Stock stock) {
        stockQueue.offer(stock);
    }

    public Stock getStock(String symbol) {
        for (Stock stock : stockQueue) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }
}
