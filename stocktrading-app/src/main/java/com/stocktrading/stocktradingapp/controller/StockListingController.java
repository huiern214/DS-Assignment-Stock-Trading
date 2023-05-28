package com.stocktrading.stocktradingapp.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.StockListingService;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*")
public class StockListingController {

    private final StockListingService stockListingService;
    

    @Autowired
    public StockListingController(StockListingService stockListingService) {
        this.stockListingService = stockListingService;
    }

    // @GetMapping
    // public Iterable<Stock> getAllStocks() {
    //     return stockListingService.getStockQueue();
    // }
    @GetMapping
    public ResponseEntity<Iterable<Stock>> getAllStocks() {
        Iterable<Stock> stocks = stockListingService.getStockQueue();
        LocalDateTime lastUpdateTime = stockListingService.getLastUpdateTime(); // Retrieve lastUpdateTime

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Last-Update", lastUpdateTime.toString()); // Set the Last-Update header

        return ResponseEntity.ok().headers(responseHeaders).body(stocks);
    }
}