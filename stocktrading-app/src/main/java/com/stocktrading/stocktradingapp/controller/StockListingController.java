package com.stocktrading.stocktradingapp.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stocktrading.stocktradingapp.model.MarketDepth;
import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.model.DTO.SearchRequest;
import com.stocktrading.stocktradingapp.service.stocks.StockListingService;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*", exposedHeaders = "last-update")
public class StockListingController {

    @Autowired
    private StockListingService stockListingService;

    @GetMapping
    public ResponseEntity<PriorityQueue<Stock>> getAllStocks() {
        PriorityQueue<Stock> stockQueue = stockListingService.getStockQueue();
        LocalDateTime lastUpdateTime = stockListingService.getLastUpdateTime();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("last-update", lastUpdateTime.toString());

        return ResponseEntity.ok().headers(responseHeaders).body(stockQueue);
    }


    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockData(@PathVariable String symbol) {
        LocalDateTime lastUpdateTime = stockListingService.getLastUpdateTime();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("last-update", lastUpdateTime.toString());
        
        return ResponseEntity.ok().headers(responseHeaders).body(stockListingService.getStock(symbol));
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> refreshStocks() throws SQLException {
        stockListingService.refreshStockData();
        return ResponseEntity.ok().body("Stock data refreshed successfully");
    }

    @PostMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestBody SearchRequest searchRequest) {
        String query = searchRequest.getQuery();
        List<Stock> searchResults = stockListingService.searchStocks(query);
        return ResponseEntity.ok().body(searchResults);
    }

    @GetMapping("/bid-data/{symbol}")
    public List<MarketDepth> getBidData(@PathVariable String symbol) {
        List<MarketDepth> bidData = new ArrayList<>();
        bidData.add(new MarketDepth("a", 10, 500, 5));
        bidData.add(new MarketDepth("b", 20, 300, 4));
        bidData.add(new MarketDepth("c", 30, 100, 2));
        return bidData;
    }

    @GetMapping("/ask-data/{symbol}")
    public List<MarketDepth> getAskData(@PathVariable String symbol) {
        List<MarketDepth> askData = new ArrayList<>();
        askData.add(new MarketDepth("a", 40, 100, 1));
        askData.add(new MarketDepth("b", 50, 300, 3));
        askData.add(new MarketDepth("c", 60, 500, 5));
        return askData;
    }

}
