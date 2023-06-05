package com.stocktrading.stocktradingapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class MarketDepth {
    private String symbol;
    private double price;
    private int qty;
    private int noOfAcc;

    public MarketDepth(String symbol, double price, int qty, int noOfAcc) {
        this.symbol = symbol;
        this.price = price;
        this.qty = qty;
        this.noOfAcc = noOfAcc;
    }

}
