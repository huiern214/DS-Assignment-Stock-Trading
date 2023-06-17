package com.stocktrading.stocktradingapp.service.news;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.stocktrading.stocktradingapp.model.News;

@Service
public class NewsService implements InitializingBean {

    public final NewsAPIService newsAPIService;
    private List<News> newsList;

    // public NewsService() {
    //     this.newsList = getNewsData();
    // }
    public NewsService(NewsAPIService newsAPIService) {
        this.newsList = new ArrayList<>();
        this.newsAPIService = newsAPIService;

        refreshNewsData();
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
        // return fetchNewsData(publishedAfter);
        return newsAPIService.fetchNewsData(publishedAfter);
    }
}

