package com.stocktrading.stocktradingapp.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocktrading.stocktradingapp.model.News;

@Service
public class NewsService implements InitializingBean {

    // @Value("${news.api.base.url}")
    // private String NEWS_API_BASE_URL;
    // @Value("${news.api.token}")
    // private String NEWS_API_TOKEN
    private String NEWS_API_TOKEN="AkCrL9OTiK7G6ftTBowN7k7jfTmqnVSj3uTixeLD";
    private String NEWS_API_BASE_URL="https://api.marketaux.com/v1/news/all";
    private List<News> newsList = new ArrayList<>();

    public NewsService() {
        this.newsList = getNewsData();
    }

    public List<News> getNewsList() {
        return newsList;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("Starting timer...");
        Timer timer = new Timer();

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the next run time at 12:00 AM MYT
        LocalDateTime nextRunTime = LocalDateTime.of(currentDate.plusDays(1), LocalTime.MIDNIGHT);

        // Calculate the initial delay until the next run time
        Duration initialDelay = Duration.between(LocalDateTime.now(), nextRunTime);
        
        // Calculate the interval for subsequent runs (24 hours)
        Duration interval = Duration.ofHours(24);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer task started at: " + LocalDateTime.now());
                refreshNewsData();
            }
        }, initialDelay.toMillis(), interval.toMillis());
    }

    // refresh using getNewsData()
    public void refreshNewsData() {
        this.newsList = getNewsData();
    }

    // get news data from API based on publishedAfter date
    public List<News> getNewsData() {
        // Custom logic to determine the publishedAfter date
        LocalDate today = LocalDate.now();
        LocalDate publishedAfterDate = today;
        if (today.isEqual(LocalDate.now())) {
            publishedAfterDate = today.minusWeeks(2);
        }
        String publishedAfter = publishedAfterDate.toString() + "T00:00:00";
        return fetchNewsData(publishedAfter);
    }

    // fetch news data from API &language=en&countries=my
    public List<News> fetchNewsData(String publishedAfter) {
        // String url = NEWS_API_BASE_URL + "?exchanges=KLSE&filter_entities=true&limit=10&published_after=" + publishedAfter + "&api_token=" + NEWS_API_TOKEN;
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

