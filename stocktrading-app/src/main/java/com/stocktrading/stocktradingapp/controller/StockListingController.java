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

    // http://localhost:8080/stocks
    @GetMapping
    public ResponseEntity<PriorityQueue<Stock>> getAllStocks() throws SQLException {
        PriorityQueue<Stock> stockQueue = stockListingService.getStockQueue();
        LocalDateTime lastUpdateTime = stockListingService.getLastUpdateTime();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("last-update", lastUpdateTime.toString());

        return ResponseEntity.ok().headers(responseHeaders).body(stockQueue);
    }
    // Example output:
    // [{"symbol":"1015.KL","name":"AMMB Holdings Berhad (AMBANK)","price":3.44,"priceChange":-0.13999987,"priceChangePercent":-3.910611,"systemQuantity":500},
    // {"symbol":"6888.KL","name":"Axiata Group Berhad (AXIATA)","price":2.65,"priceChange":0.01999998,"priceChangePercent":0.76045555,"systemQuantity":500},
    // {"symbol":"1023.KL","name":"CIMB Group Holdings Berhad (CIMB)","price":4.98,"priceChange":-0.01999998,"priceChangePercent":-0.3999996,"systemQuantity":500},
    // {"symbol":"6947.KL","name":"Celcomdigi Berhad (CDB)","price":4.3,"priceChange":0.0,"priceChangePercent":0.0,"systemQuantity":500}]

    // http://localhost:8080/stocks/{symbol}
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockData(@PathVariable String symbol) throws SQLException {
        LocalDateTime lastUpdateTime = stockListingService.getLastUpdateTime();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("last-update", lastUpdateTime.toString());
        
        return ResponseEntity.ok().headers(responseHeaders).body(stockListingService.getStock(symbol));
    }
    // Example output:
    // {"symbol":"1015.KL","name":"AMMB Holdings Berhad (AMBANK)","price":3.44,"priceChange":-0.13999987,"priceChangePercent":-3.910611,"systemQuantity":500}

    // http://localhost:8080/stocks/refresh
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshStocks() throws SQLException {
        stockListingService.refreshStockData();
        return ResponseEntity.ok().body("Stock data refreshed successfully");
    }

    // http://localhost:8080/stocks/search
    @PostMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestBody SearchRequest searchRequest) {
        String query = searchRequest.getQuery();
        List<Stock> searchResults = stockListingService.searchStocks(query);
        return ResponseEntity.ok().body(searchResults);
    }
    // Example input:
    // {
    //     "query": "bank"
    // }
    // Example output:
    // [
    //     {
    //         "symbol": "1015.KL",
    //         "name": "AMMB Holdings Berhad (AMBANK)",
    //         "price": 3.44,
    //         "priceChange": -0.13999987,
    //         "priceChangePercent": -3.910611,
    //         "systemQuantity": 500
    //     },
    //     {
    //         "symbol": "5819.KL",
    //         "name": "Hong Leong Bank Berhad (HLBANK)",
    //         "price": 18.5,
    //         "priceChange": -0.10000038,
    //         "priceChangePercent": -0.53763646,
    //         "systemQuantity": 495
    //     }
    // ]

    // http://localhost:8080/stocks/bid-data/{symbol}
    @GetMapping("/bid-data/{symbol}")
    public List<MarketDepth> getBidData(@PathVariable String symbol) {
        List<MarketDepth> bidData = new ArrayList<>();
        bidData.add(new MarketDepth("a", 10, 500, 5));
        bidData.add(new MarketDepth("b", 20, 300, 4));
        bidData.add(new MarketDepth("c", 30, 100, 2));
        return bidData;
    }

    // http://localhost:8080/stocks/ask-data/{symbol}
    @GetMapping("/ask-data/{symbol}")
    public List<MarketDepth> getAskData(@PathVariable String symbol) {
        List<MarketDepth> askData = new ArrayList<>();
        askData.add(new MarketDepth("a", 40, 100, 1));
        askData.add(new MarketDepth("b", 50, 300, 3));
        askData.add(new MarketDepth("c", 60, 500, 5));
        return askData;
    }

}
