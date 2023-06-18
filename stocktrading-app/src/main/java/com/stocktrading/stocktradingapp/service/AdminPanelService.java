package com.stocktrading.stocktradingapp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import com.stocktrading.stocktradingapp.model.Stock;
import com.stocktrading.stocktradingapp.model.User;
import com.stocktrading.stocktradingapp.service.databaseOperations.StockTableOperationService;
import com.stocktrading.stocktradingapp.service.stocks.StockListingService;

@Service
public class AdminPanelService {

    public final UserService userService;
    public final StockListingService stockListingService;
    public final StockTableOperationService stockTableOperationService;
    public final MatchOrdersService matchOrdersService;
    private Connection connection;

    public AdminPanelService(Connection connection, UserService userService, StockListingService stockListingService, StockTableOperationService stockTableOperationService, MatchOrdersService matchOrdersService) throws SQLException {
        this.userService = userService;
        this.stockListingService = stockListingService;
        this.stockTableOperationService = stockTableOperationService;
        this.matchOrdersService = matchOrdersService;
        this.connection = connection;
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
            stockListingService.refreshStockData();
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

    public PriorityQueue<Stock> getStocks() throws SQLException {
        return stockListingService.getStockQueue();
    }

    public void updateStockQuantity(String code, int quantity) throws SQLException {
        stockTableOperationService.updateStockQuantity(code, quantity);
        matchOrdersService.fulfillOrders();
    }

    // Update all stocks quantity to 500 lot in the database
    public void updateAllStocksQuantity(int quantity) {
        stockTableOperationService.updateAllStocksQuantity(quantity);
    }

    // Deletes / Disqualify users with funds greater than or equal to the specified amount
    public boolean deleteUsersWithHighFunds(double funds) throws SQLException {
        return userService.deleteUsersWithHighFunds(funds);
    }
}
