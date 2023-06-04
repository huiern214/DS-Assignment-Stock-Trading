package com.stocktrading.stocktradingapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class MarketDepth {
    private String symbol;
    private double Price;
    private int Qty;
    private int noOfAcc;

    public MarketDepth(String symbol, double Price, int Qty, int noOfAcc) {
        this.symbol = symbol;
        this.Price = Price;
        this.Qty = Qty;
        this.noOfAcc = noOfAcc;
    }

}
