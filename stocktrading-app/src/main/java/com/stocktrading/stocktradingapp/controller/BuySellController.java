package com.stocktrading.stocktradingapp.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stocktrading.stocktradingapp.model.DTO.BuySellRequest;
import com.stocktrading.stocktradingapp.service.BuySellService;

@RestController
@RequestMapping("/api/buysell")
@CrossOrigin(origins = "*")
public class BuySellController {

    private final BuySellService buySellService;

    @Autowired
    public BuySellController(BuySellService buySellService) {
        this.buySellService = buySellService;
    }

    // http://localhost:8080/api/buysell/buy
    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestBody BuySellRequest buyRequest) {
        try {
            buySellService.buyStock(
                    buyRequest.getStockSymbol(),
                    buyRequest.getDesiredPrice(),
                    buyRequest.getDesiredQuantity(),
                    buyRequest.getBuyerId()
            );
            return ResponseEntity.ok("Stock bought successfully.");
        } catch (SQLException e) {
            // Handle the exception and return an appropriate error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to buy the stock. Reason: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Example input:
    // {
    //     "stockSymbol": "5255.KL",
    //     "desiredPrice": 3.75,
    //     "desiredQuantity": 7,
    //     "buyerId": 2
    //   }

    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody BuySellRequest sellRequest) {
        try {
            buySellService.sellStock(
                    sellRequest.getStockSymbol(),
                    sellRequest.getDesiredPrice(),
                    sellRequest.getDesiredQuantity(),
                    sellRequest.getSellerId()
            );
            return ResponseEntity.ok("Stock sold successfully.");
        } catch (SQLException e) {
            // Handle the exception and return an appropriate error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to sell the stock. Reason: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Example input:
    // {
    //     "stockSymbol": "1961.KL",
    //     "desiredPrice": 3.75,
    //     "desiredQuantity": 5,
    //     "sellerId": 1
    //   }
}

