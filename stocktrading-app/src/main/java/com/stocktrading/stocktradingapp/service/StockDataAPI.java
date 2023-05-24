package com.stocktrading.stocktradingapp.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StockDataAPI {

    public static void main(String[] args) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://apidojo-yahoo-finance-v1.p.rapidapi.com/auto-complete?q=tesla&region=US"))
                .header("X-RapidAPI-Key", "741fdcaf75msh52a77b3bf384167p1e4b9ajsn8f7c595eb4d1")
                .header("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}
