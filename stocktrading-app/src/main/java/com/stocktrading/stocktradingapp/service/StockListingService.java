package com.stocktrading.stocktradingapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockListingService implements InitializingBean {

    // final String[] COMPANY_CODES = {
    //     "5296", "1015", "1066", "1295", "1961", "3182", "5225", "4707", "4863", "5347", 
    //     "6888", "1023", "7277", "6947", "3034", "5168", "5819", "1082", "5235SS", "2445", 
    //     "1155", "6012", "3816", "5183", "5681", "6033", "4065", "8869", "4197", "5285"};
    private List<String> COMPANY_CODES = new ArrayList<>();

    private final StockService stockService;
    private final StockTableOperationService stockTableOperationService;

    private PriorityQueue<Stock> stockQueue;

    private LocalDateTime lastUpdateTime;
    // private static final LocalTime REFRESH_TIME = LocalTime.of(8, 0); // Set the desired refresh time
    private static final long INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds

    // Constructor
    public StockListingService(StockService stockService, StockTableOperationService stockTableOperationService) throws SQLException {
        this.stockService = stockService;
        this.stockTableOperationService = stockTableOperationService;
        this.stockQueue = new PriorityQueue<>();
        this.lastUpdateTime = LocalDateTime.now(); // Set the initial value

        initializeStockQueue();
    }

    // Getters and setters
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public PriorityQueue<Stock> getStockQueue() {
        return stockQueue;
    }

    public List<String> getCOMPANY_CODES() {
        return COMPANY_CODES;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // Initialize the stockQueue from the stock table in the database
    private void initializeStockQueue() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:stocktrading-app/database/data.sqlite3")) {
            String selectQuery = "SELECT code FROM CompanyCodes";
            try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(selectQuery)) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    COMPANY_CODES.add(code);
                }
            }
        }

        // Build the symbols string
        StringBuilder symbolsBuilder = new StringBuilder();
        for (String code : COMPANY_CODES) {
            symbolsBuilder.append(code).append(".KL,");
        }

        // Delete last character
        symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
        String company_symbols = symbolsBuilder.toString();

        // Get the stock data for all symbols
        this.stockQueue = stockService.getStockData(company_symbols);

        // Add the stock data to the stock table if not exist, update if exist
        if (stockQueue != null){
            for (Stock stock : this.stockQueue) {
                stockTableOperationService.addStock(stock.getName(), stock.getSymbol(), stock.getPrice());
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Starting timer...");
        Timer timer = new Timer();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextInterval = getNextInterval(now);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer task started at: " + LocalDateTime.now());
                try {
                    refreshStockData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, Duration.between(now, nextInterval).toMillis(), INTERVAL);
    }

    private LocalDateTime getNextInterval(LocalDateTime dateTime) {
        int minutes = dateTime.getMinute();
        int seconds = dateTime.getSecond();
        int remainder = minutes % 5;
        if (remainder == 0 && seconds == 0) {
            return dateTime.plusMinutes(5);
        } else {
            int minutesToAdd = 5 - remainder;
            return dateTime.plusMinutes(minutesToAdd).withSecond(0);
        }
    }

    // Add a stock to the stockQueue
    public void addStock(Stock stock) throws SQLException {
        stockTableOperationService.addStock(stock.getName(), stock.getSymbol(), stock.getPrice());
        stockQueue.offer(stock);
    }

    // Get a stock from the stockQueue
    public Stock getStock(String symbol) {
        for (Stock stock : stockQueue) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }
    
    // Refresh the stock data in the stockQueue
    public void refreshStockData() throws SQLException {
        setLastUpdateTime(LocalDateTime.now());

        // // Create a string of symbols separated by commas
        // StringBuilder symbolsBuilder = new StringBuilder();
        // for (Stock stock : stockQueue) {
        //     symbolsBuilder.append(stock.getSymbol()).append(",");
        // }

        // Build the symbols string
        StringBuilder symbolsBuilder = new StringBuilder();
        for (String code : COMPANY_CODES) {
            symbolsBuilder.append(code).append(".KL,");
        }

        // Delete last character
        symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
        String company_symbols = symbolsBuilder.toString();
    
        // Get the updated stock data for all symbols
        PriorityQueue<Stock> updatedStocks = stockService.getStockData(company_symbols);
    
        // Update the stock data in the stockQueue
        if (updatedStocks != null) {
            for (Stock updatedStock : updatedStocks) {
                Stock existingStock = getStock(updatedStock.getSymbol());
                if (existingStock != null) {
                    existingStock.setPrice(updatedStock.getPrice());
                    existingStock.setPriceChange(updatedStock.getPriceChange());
                    existingStock.setPriceChangePercent(updatedStock.getPriceChangePercent());
                    stockTableOperationService.updateStockPrice(existingStock.getSymbol(), existingStock.getPrice());
                } else {
                    addStock(updatedStock);
                }
            }
        }
    }    

    // Search for stocks by symbol or name
    public List<Stock> searchStocks(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>(stockQueue);
        }
        List<Stock> searchResults = new ArrayList<>();
        for (Stock stock : stockQueue) {
            String symbol = stock.getSymbol().toLowerCase();
            String name = stock.getName().toLowerCase();

            if (KMPSearch(symbol, query.toLowerCase()) != -1 ||
                KMPSearch(name, query.toLowerCase()) != -1) {
                searchResults.add(stock);
            }
        }
        return searchResults;
    }

    private int KMPSearch(String text, String pattern) {
        int N = text.length();
        int M = pattern.length();

        int[] lps = computeLPSArray(pattern);

        int i = 0; // Index for text[]
        int j = 0; // Index for pattern[]

        while (i < N) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }

            if (j == M) {
                return i - j; // Match found at index i - j
            } else if (i < N && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1; // No match found
    }

    private int[] computeLPSArray(String pattern) {
        int M = pattern.length();
        int[] lps = new int[M];
        int len = 0;
        int i = 1;

        while (i < M) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }
}
