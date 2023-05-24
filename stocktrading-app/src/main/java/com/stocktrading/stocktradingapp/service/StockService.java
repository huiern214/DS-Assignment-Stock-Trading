package com.stocktrading.stocktradingapp.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.stocktradingapp.model.Stock;

@Service
public class StockService {

    private static final String API_BASE_URL = "https://yahoo-finance15.p.rapidapi.com/api/yahoo/qu/quote/";
    private static final String API_KEY = "[YOUR_API_KEY]";
    // Get your own API key here - https://rapidapi.com/sparior/api/yahoo-finance15

    public Stock getStockData(String symbol) {
        String url = API_BASE_URL + symbol;

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

        Stock stock = parseResponse(response);
        return stock;
    }

    private Stock parseResponse(HttpResponse<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(response.body());
            JsonNode stockNode = jsonNode.get(0); // Assuming the response contains a single stock object
    
            String symbol = stockNode.get("symbol").asText();
            String companyName = stockNode.get("shortName").asText();
            double price = stockNode.get("regularMarketPrice").asDouble();
            double priceChange = stockNode.get("regularMarketChange").asDouble();
            double priceChangePercent = stockNode.get("regularMarketChangePercent").asDouble();
    
            return new Stock(symbol, companyName, price, priceChange, priceChangePercent);
        } catch (IOException e) {
            // Handle exception
            return null;
        }
    }
}
