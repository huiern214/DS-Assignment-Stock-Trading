package com.stocktrading.stocktradingapp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockListingService {

    final String[] COMPANY_CODES = {
        "0661", "1015", "1066", "1295", "1961", "3182", "5225", "4707", "4863", "5347", 
        "6888", "1023", "7277", "6947", "3034", "5168", "5819", "1082", "5235SS", "2445", 
        "1155", "6012", "3816", "5183", "5681", "6033", "4065", "8869", "4197", "5285"};

    private final StockService stockService;
    private PriorityQueue<Stock> stockQueue;

    private LocalDateTime lastUpdateTime;

    public StockListingService(StockService stockService) {
        this.stockService = stockService;
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

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    // Initialize the stockQueue with the stock data from the stockService
    private void initializeStockQueue() {
        // Create a string of symbols separated by commas
        StringBuilder symbolsBuilder = new StringBuilder();
        for (String code : COMPANY_CODES) {
            symbolsBuilder.append(code).append(".KL,");
        }
        // delete last character
        symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
        String company_symbols = symbolsBuilder.toString();

        // Get the stock data for all symbols
        this.stockQueue = stockService.getStockData(company_symbols);
    }

    // Add a stock to the stockQueue
    public void addStock(Stock stock) {
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
    public void refreshStockData() {
        setLastUpdateTime(LocalDateTime.now());
    
        // Create a string of symbols separated by commas
        StringBuilder symbolsBuilder = new StringBuilder();
        for (Stock stock : stockQueue) {
            symbolsBuilder.append(stock.getSymbol()).append(",");
        }
        symbolsBuilder.deleteCharAt(symbolsBuilder.length() - 1);
        String symbols = symbolsBuilder.toString();
    
        // Get the updated stock data for all symbols
        PriorityQueue<Stock> updatedStocks = stockService.getStockData(symbols);
    
        // Update the stock data in the stockQueue
        if (updatedStocks != null) {
            for (Stock updatedStock : updatedStocks) {
                Stock existingStock = getStock(updatedStock.getSymbol());
                if (existingStock != null) {
                    existingStock.setPrice(updatedStock.getPrice());
                    existingStock.setPriceChange(updatedStock.getPriceChange());
                    existingStock.setPriceChangePercent(updatedStock.getPriceChangePercent());
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
