package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.model.News;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.stocktrading.stocktradingapp.service.news.NewsService;

@RestController
@RequestMapping("/news")
@CrossOrigin(origins = "*")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // http://localhost:8080/news
    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        List<News> newsList = newsService.getNewsList();

        if (newsList != null && !newsList.isEmpty()) {
            return ResponseEntity.ok(newsList);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
    // Sample output:
    // [{"title":"Bursa lower at midday","description":"Bursa Malaysia stayed in negative territory at lunch break on Thursday (June 15) as investors digested the US Federal Reserve's (Fed) rate hike respite, despite the recovery in regional markets.","url":"https://theedgemalaysia.com/node/671271","imageUrl":"https://assets.theedgemarkets.com/noon-market-down_20230615125530_theedgemalaysia.jpg","publishedAt":"2023-06-15T04:56:40.000000Z","source":"theedgemarkets.com"},
    // {"title":"Bursa stays higher at midday on US rate hike pause optimism","description":"Bursa Malaysia remained higher at the lunch break, in line with the regional market performance on optimism of a pause in US interest rate hikes later on Wednesday (June 14) night.","url":"https://theedgemalaysia.com/node/671108","imageUrl":"https://assets.theedgemarkets.com/noon-market-up_20230614130511_theedgemalaysia.jpg","publishedAt":"2023-06-14T05:08:40.000000Z","source":"theedgemarkets.com"},
    // {"title":"Bursa opens higher in early trade","description":"Bursa Malaysia turned positive on Wednesday (June 14) morning, taking its cue from the Wall Street's positive overnight performance after the US inflation data came in within expectation.","url":"https://theedgemalaysia.com/node/671074","imageUrl":"https://assets.theedgemarkets.com/opening-market-up_20230614093207_theedgemalaysia.jpg","publishedAt":"2023-06-14T01:32:42.000000Z","source":"theedgemarkets.com"}]
}
