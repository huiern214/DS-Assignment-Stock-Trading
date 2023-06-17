package com.stocktrading.stocktradingapp.service.stocks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockAPIService {

    @Value("${stock.trading.api.base.url}")
    private String API_BASE_URL;
    @Value("${stock.trading.api.key}")
    private String API_KEY;

    public PriorityQueue<Stock> getStockData(String symbols) {
        String url = API_BASE_URL + symbols;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            // Handle exception
            return null;
        }

        PriorityQueue<Stock> stocks = parseResponse(response);
        return stocks;
    }

    private PriorityQueue<Stock> parseResponse(HttpResponse<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.body());
    
            if (jsonNode.isArray()) {
                List<Stock> stockList = new ArrayList<>();
    
                for (JsonNode stockNode : jsonNode) {
                    String symbol = stockNode.get("symbol").asText();
                    String companyName = stockNode.get("longName").asText() + " (" + stockNode.get("shortName").asText() + ")";
                    double price = stockNode.get("regularMarketPrice").asDouble();
                    double priceChange = stockNode.get("regularMarketChange").asDouble();
                    double priceChangePercent = stockNode.get("regularMarketChangePercent").asDouble();
    
                    Stock stock = new Stock(symbol, companyName, price, priceChange, priceChangePercent);
                    stockList.add(stock);
                }
    
                // Since PriorityQueue does not allow sorting, we will sort the list first
                // Sort the list based on the name in ascending order
                stockList.sort(Comparator.comparing(Stock::getName));

                // Create a PriorityQueue based on the sorted list
                PriorityQueue<Stock> stockQueue = new PriorityQueue<>(Comparator.comparing(Stock::getName));
                stockQueue.addAll(stockList);
    
                for (Stock stock : stockQueue) {
                    System.out.print(stock.getPrice() + ", ");
                }
                return stockQueue;
            }
        } catch (IOException e) {
            // Handle exception
        }
    
        return null;
    }
}   