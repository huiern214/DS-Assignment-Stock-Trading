package com.stocktrading.stocktradingapp.controller;

import java.time.LocalDateTime;
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
    public Stock getStockData(@PathVariable String symbol) {
        return stockListingService.getStock(symbol);
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

}
