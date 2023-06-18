package com.stocktrading.stocktradingapp.service.stocks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.databaseOperations.StockTableOperationService;

@Service
public class StockListingService implements InitializingBean {

    // final String[] COMPANY_CODES = {
    //     "5296", "1015", "1066", "1295", "1961", "3182", "5225", "4707", "4863", "5347", 
    //     "6888", "1023", "7277", "6947", "3034", "5168", "5819", "1082", "5235SS", "2445", 
    //     "1155", "6012", "3816", "5183", "5681", "6033", "4065", "8869", "4197", "5285"};
    private List<String> COMPANY_CODES = new ArrayList<>();

    private final StockAPIService stockService;
    private final StockTableOperationService stockTableOperationService;

    private PriorityQueue<Stock> stockQueue;

    private LocalDateTime lastUpdateTime;
    // private static final LocalTime REFRESH_TIME = LocalTime.of(8, 0); // Set the desired refresh time
    private static final long INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds

    // Constructor
    public StockListingService(StockAPIService stockService, StockTableOperationService stockTableOperationService) throws SQLException {
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

    public PriorityQueue<Stock> getStockQueue() throws SQLException {
        // return stockQueue;
        stockQueue.clear();
        stockQueue.addAll(stockTableOperationService.getAllStocks());
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
        if (symbolsBuilder.length() > 0)
            symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
        String company_symbols = symbolsBuilder.toString();

        // Get all stocks from the stock table
        List<Stock> existingStocks = stockTableOperationService.getAllStocks();

        List<String> stockSymbols = new ArrayList<String>();
        for (String s : company_symbols.split(",")) {
            stockSymbols.add(s);
        }
        // Check if the existing stocks cover all the company codes
        boolean hasAllStocks = existingStocks.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.toSet())
                .containsAll(stockSymbols);

        if (!hasAllStocks) {
            // Get the stock data for all symbols
            List<Stock> newStocks = stockService.getStockData(company_symbols);

            // Update the stock table with new stock data
            for (Stock stock : newStocks) {
                stockTableOperationService.addStock(stock.getName(), stock.getSymbol(), stock.getPrice(), stock.getPriceChange(), stock.getPriceChangePercent());
            }
        }

        // Update the stock queue with all stocks
        stockQueue.addAll(stockTableOperationService.getAllStocks());
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
        // stockTableOperationService.addStock(stock.getName(), stock.getSymbol(), stock.getPrice());
        stockTableOperationService.addStock(stock.getName(), stock.getSymbol(), stock.getPrice(), stock.getPriceChange(), stock.getPriceChangePercent());
        stockQueue.offer(stock);
    }

    // Get a stock from the stockQueue
    public Stock getStock(String symbol) throws SQLException {
        for (Stock stock : getStockQueue()) {
            if (stock.getSymbol().equals(symbol)) {
                return stock;
            }
        }
        return null;
    }
    
    public void refreshStockData() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
    
        // Check if it is within regular market hours
        if (isWithinTradingHours(now)) {
            setLastUpdateTime(now);
    
            // Build the symbols string
            StringBuilder symbolsBuilder = new StringBuilder();
            for (String code : COMPANY_CODES) {
                symbolsBuilder.append(code).append(".KL,");
            }
    
            // Delete last character
            symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
            String company_symbols = symbolsBuilder.toString();
    
            // Get the updated stock data for all symbols
            List<Stock> updatedStocks = stockService.getStockData(company_symbols);
    
            // Update the stock data in the stockQueue and stock table
            if (updatedStocks != null) {
                // Clear the existing stockQueue
                stockQueue.clear();
    
                for (Stock updatedStock : updatedStocks) {
                    Stock existingStock = stockTableOperationService.getStock(updatedStock.getSymbol());
                    if (existingStock != null) {
                        existingStock.setPrice(updatedStock.getPrice());
                        existingStock.setPriceChange(updatedStock.getPriceChange());
                        existingStock.setPriceChangePercent(updatedStock.getPriceChangePercent());
                        stockTableOperationService.updateStockPrice(existingStock.getSymbol(), existingStock.getPrice());
                        stockTableOperationService.updateStockPriceChange(existingStock.getSymbol(), existingStock.getPriceChange());
                        stockTableOperationService.updateStockPriceChangePercent(existingStock.getSymbol(), existingStock.getPriceChangePercent());
    
                        // Add the existing stock to the stockQueue
                        stockQueue.add(existingStock);
                    } else {
                        stockTableOperationService.addStock(updatedStock.getName(), updatedStock.getSymbol(), updatedStock.getPrice(), updatedStock.getPriceChange(), updatedStock.getPriceChangePercent());
    
                        // Add the newly added stock to the stockQueue
                        Stock newlyAddedStock = stockTableOperationService.getStock(updatedStock.getSymbol());
                        if (newlyAddedStock != null) {
                            stockQueue.add(newlyAddedStock);
                        }
                    }
                }
            }
        } else {
            // Retrieve stock data from the stock table during non-trading hours
            stockQueue.clear();
            stockQueue.addAll(stockTableOperationService.getAllStocks());
        }
    }
    
    private boolean isWithinTradingHours(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
    
        // Check if it is a weekday (Monday to Friday)
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
    
        // Check if it is within regular market hours (9:00 AM - 12:30 PM and 2:30 PM - 5:00 PM MST)
        if ((hour >= 9 && hour < 12) || (hour == 12 && minute <= 30) || (hour >= 14 && hour < 17)) {
            return true;
        }
    
        return false;
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
