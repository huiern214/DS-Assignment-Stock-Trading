package com.stocktrading.stocktradingapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.model.User;

@Service
public class AdminPanelService {

    public final UserService userService;
    public final StockListingService stockListingService;
    public final StockTableOperationService stockTableOperationService;
    
    private final String databaseUrl = "jdbc:sqlite:stocktrading-app/database/data.sqlite3";
    private Connection connection;

    public AdminPanelService(UserService userService, StockListingService stockListingService, StockTableOperationService stockTableOperationService) throws SQLException {
        this.userService = userService;
        this.stockListingService = stockListingService;
        this.stockTableOperationService = stockTableOperationService;
        this.connection = DriverManager.getConnection(databaseUrl);
    }

    public boolean deleteUser(int userId) throws SQLException {
        return userService.deleteUser(userId);

    }

    public List<User> usersList() throws SQLException {
        return userService.getAllUsers();
    }
    
    public void addStock(String code) {
        if (stockListingService.getCOMPANY_CODES().contains(code)) {
            System.out.println("Stock already exists");
            return;
        }
        
        String insertQuery = "INSERT INTO CompanyCodes (code) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, code);
            statement.executeUpdate();
            // Update the COMPANY_CODES list in StockListingService
            stockListingService.getCOMPANY_CODES().add(code);
        }
         catch (SQLException e) {
            e.printStackTrace();
        }
    } 

    public void deleteStock(String code) {
        if (stockListingService.getCOMPANY_CODES().contains(code)) {
            String deleteQuery = "DELETE FROM CompanyCodes WHERE code = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                statement.setString(1, code);
                statement.executeUpdate();
                // Update the COMPANY_CODES list in StockListingService
                stockListingService.getCOMPANY_CODES().remove(code);
                stockListingService.getStockQueue().remove(stockListingService.getStock(code + ".KL"));
                stockTableOperationService.removeStock(code+".KL");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Stock not found");
        }
    }

    public List<String> stockList() {
        String selectQuery = "SELECT code FROM CompanyCodes";
        List<String> stocks = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                stocks.add(resultSet.getString("code"));
            }
        }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public PriorityQueue<Stock> getStocks() {
        return stockListingService.getStockQueue();
    }
}
