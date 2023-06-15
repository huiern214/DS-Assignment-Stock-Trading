package com.stocktrading.stocktradingapp.tableCreator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CompanyCodesLoader {

    final String[] COMPANY_CODES = {
        "5296", "1015", "1066", "1295", "1961", "3182", "5225", "4707", "4863", "5347", 
        "6888", "1023", "7277", "6947", "3034", "5168", "5819", "1082", "5235SS", "2445", 
        "1155", "6012", "3816", "5183", "5681", "6033", "4065", "8869", "4197", "5285"
    };

    public void loadCompanyCodes() {
        final String databaseUrl = "jdbc:sqlite:stocktrading-app/database/data.sqlite3";
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String insertQuery = "INSERT INTO CompanyCodes (code) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (String code : COMPANY_CODES) {
                    statement.setString(1, code);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CompanyCodesLoader loader = new CompanyCodesLoader();
        loader.loadCompanyCodes();
    }
}
