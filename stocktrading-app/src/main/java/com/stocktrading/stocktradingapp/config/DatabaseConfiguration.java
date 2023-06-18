package com.stocktrading.stocktradingapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    
    @Bean
    public Connection databaseConnection() throws SQLException {
        String databaseUrl = "jdbc:sqlite:stocktrading-app/database/data.sqlite3";
        return DriverManager.getConnection(databaseUrl);
    }
}