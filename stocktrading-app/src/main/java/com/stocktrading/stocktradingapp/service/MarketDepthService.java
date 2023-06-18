package com.stocktrading.stocktradingapp.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.MarketDepth;
import com.stocktrading.stocktradingapp.model.Order;
import com.stocktrading.stocktradingapp.service.databaseOperations.OrdersTableOperationService;

@Service
public class MarketDepthService {

    private final OrdersTableOperationService ordersTableOperationService;

    public MarketDepthService(OrdersTableOperationService ordersTableOperationService) {
        this.ordersTableOperationService = ordersTableOperationService;
    }

    public List<MarketDepth> getBidMarketDepthBySymbol(String symbol) throws SQLException {
        List<Order> orders = ordersTableOperationService.getMatchingBuyOrders(symbol);
        List<MarketDepth> marketDepth = new ArrayList<>();
        // check if the order have same user id
        List<Integer> userIds = new ArrayList<>();

        for (Order order : orders) {
            if (marketDepthEntryExists(marketDepth, order.getPrice())) {
                int index = getMarketDepthEntryIndex(marketDepth, order.getPrice());
                MarketDepth marketDepthEntry = marketDepth.get(index);
                marketDepthEntry.setQty(marketDepthEntry.getQty() + order.getQuantity());
                if (!userIds.contains(order.getUserId())) {
                    marketDepthEntry.setNoOfAcc(marketDepthEntry.getNoOfAcc() + 1);
                    userIds.add(order.getUserId());
                }
            } else {
                MarketDepth marketDepthEntry = new MarketDepth(order.getStockSymbol(), order.getPrice(), order.getQuantity(), 1);
                marketDepth.add(marketDepthEntry);
            }
        }
        // Sort the market depth by price in descending order
        Collections.sort(marketDepth, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));

        return marketDepth;
    }

    public List<MarketDepth> getAskMarketDepthBySymbol(String symbol) throws SQLException {
        List<Order> orders = ordersTableOperationService.getMatchingSellOrders(symbol);
        List<MarketDepth> marketDepth = new ArrayList<>();
        // check if the order have same user id
        List<Integer> userIds = new ArrayList<>();

        for (Order order : orders) {
            if (marketDepthEntryExists(marketDepth, order.getPrice())) {
                int index = getMarketDepthEntryIndex(marketDepth, order.getPrice());
                MarketDepth marketDepthEntry = marketDepth.get(index);
                marketDepthEntry.setQty(marketDepthEntry.getQty() + order.getQuantity());
                if (!userIds.contains(order.getUserId())) {
                    marketDepthEntry.setNoOfAcc(marketDepthEntry.getNoOfAcc() + 1);
                    userIds.add(order.getUserId());
                }
            } else {
                MarketDepth marketDepthEntry = new MarketDepth(order.getStockSymbol(), order.getPrice(), order.getQuantity(), 1);
                marketDepth.add(marketDepthEntry);
            }
        }
        // Sort the market depth by price in ascending order
        Collections.sort(marketDepth, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));

        return marketDepth;
    }

    private boolean marketDepthEntryExists(List<MarketDepth> marketDepth, double price) {
        for (MarketDepth marketDepthEntry : marketDepth) {
            if (marketDepthEntry.getPrice() == price) {
                return true;
            }
        }
        return false;
    }

    private int getMarketDepthEntryIndex(List<MarketDepth> marketDepth, double price) {
        for (int i = 0; i < marketDepth.size(); i++) {
            if (marketDepth.get(i).getPrice() == price) {
                return i;
            }
        }
        return -1;
    }
}
