package com.stocktrading.stocktradingapp.controller;

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
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stocktrading.stocktradingapp.model.MarketDepth;
import com.stocktrading.stocktradingapp.model.SearchRequest;
import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.StockListingService;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*", exposedHeaders = "last-update")
public class StockListingController {

    private final StockListingService stockListingService;

    @Autowired
    public StockListingController(StockListingService stockListingService) {
        this.stockListingService = stockListingService;
    }

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
    public ResponseEntity<String> refreshStocks() {
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
        bidData.add(new MarketDepth("a", 100, 100, 1));
        bidData.add(new MarketDepth("b", 200, 100, 2));
        bidData.add(new MarketDepth("c", 300, 100, 3));
        return bidData;
    }

    @GetMapping("/ask-data/{symbol}")
    public List<MarketDepth> getAskData(@PathVariable String symbol) {
        List<MarketDepth> askData = new ArrayList<>();
        askData.add(new MarketDepth("a", 400, 100, 1));
        askData.add(new MarketDepth("b", 500, 100, 2));
        askData.add(new MarketDepth("c", 600, 100, 3));
        return askData;
    }

}
