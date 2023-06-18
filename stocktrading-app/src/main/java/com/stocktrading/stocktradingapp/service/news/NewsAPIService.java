package com.stocktrading.stocktradingapp.service.news;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.stocktradingapp.model.News;

@Service
public class NewsAPIService {

    @Value("${news.api.base.url}")
    private String NEWS_API_BASE_URL;

    @Value("${news.api.token}")
    private String NEWS_API_TOKEN;
    
    // fetch news data from API &language=en&countries=my
    public List<News> fetchNewsData(String publishedAfter) {
        String url = NEWS_API_BASE_URL + "?filter_entities=true&limit=10&published_after=" + publishedAfter + "&api_token=" + NEWS_API_TOKEN + "&language=en&countries=my";
        System.out.println("Fetching news data from: " + url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return parseResponse(response);
    }

    private List<News> parseResponse(HttpResponse<String> response) {
        List<News> newsList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode dataNode = root.get("data");

            if (dataNode.isArray()) {
                for (JsonNode newsNode : dataNode) {
                    String title = newsNode.get("title").asText();
                    String description = newsNode.get("description").asText();
                    String url = newsNode.get("url").asText();
                    String imageUrl = newsNode.get("image_url").asText();
                    String publishedAt = newsNode.get("published_at").asText();
                    String source = newsNode.get("source").asText();

                    News news = new News(title, description, url, imageUrl, publishedAt, source);
                    newsList.add(news);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newsList;
    }
}
