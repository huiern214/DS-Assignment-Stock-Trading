package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public Stock getStockData(@PathVariable String symbol) {
        return stockService.getStockData(symbol);
    }
}
