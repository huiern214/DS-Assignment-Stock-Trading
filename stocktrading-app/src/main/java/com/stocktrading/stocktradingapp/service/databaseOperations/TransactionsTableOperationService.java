package com.stocktrading.stocktradingapp.service.databaseOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.stocktrading.stocktradingapp.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionsTableOperationService {

    private Connection connection;

    public TransactionsTableOperationService(Connection connection) {
        this.connection = connection;
    }

    // inserts a transaction into the database
    public void insertTransaction(int userId, String stockSymbol, double price,int quantity, String transactionType) throws SQLException {
        String insertTransactionQuery = "INSERT INTO Transactions (user_id, stock_symbol, transaction_type, quantity, price, timestamp) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(insertTransactionQuery)) {
            statement.setInt(1, userId);
            statement.setString(2, stockSymbol);
            statement.setString(3, transactionType);
            statement.setInt(4, quantity);
            statement.setDouble(5, price);

            statement.executeUpdate();
        }
    }

    // updates a transaction in the database
    public void updateTransaction(int transactionId, int quantity, double price) throws SQLException {
        String updateTransactionQuery = "UPDATE Transactions SET quantity = ?, price = ? WHERE transaction_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateTransactionQuery)) {
            statement.setInt(1, quantity);
            statement.setDouble(2, price);
            statement.setInt(3, transactionId);

            statement.executeUpdate();
        }
    }

    // deletes a transaction from the database
    public void deleteTransaction(int transactionId) throws SQLException {
        String deleteTransactionQuery = "DELETE FROM Transactions WHERE transaction_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(deleteTransactionQuery)) {
            statement.setInt(1, transactionId);

            statement.executeUpdate();
        }
    }

    // returns a list of transactions for a given user
    public List<Transaction> getTransactionsByUser(int userId) throws SQLException {
        String getTransactionsQuery = "SELECT * FROM Transactions WHERE user_id = ?";

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(getTransactionsQuery)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("transaction_id");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    String transactionType = resultSet.getString("transaction_type");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    String timestamp = resultSet.getString("timestamp");

                    Transaction transaction = new Transaction(transactionId, userId, stockSymbol, transactionType, quantity, price, timestamp);
                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    // returns a list of transactions for a given stock
    public List<Transaction> getTransactionsByStock(String stockSymbol) throws SQLException {
        String getTransactionsQuery = "SELECT * FROM Transactions WHERE stock_symbol = ?";

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(getTransactionsQuery)) {
            statement.setString(1, stockSymbol);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("transaction_id");
                    int userId = resultSet.getInt("user_id");
                    String transactionType = resultSet.getString("transaction_type");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    String timestamp = resultSet.getString("timestamp");

                    Transaction transaction = new Transaction(transactionId, userId, stockSymbol, transactionType, quantity, price, timestamp);
                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    // returns a list of transactions for a given date range
    public List<Transaction> getTransactionsByDateRange(String startDate, String endDate) throws SQLException {
        String getTransactionsQuery = "SELECT * FROM Transactions WHERE timestamp BETWEEN ? AND ?";

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(getTransactionsQuery)) {
            statement.setString(1, startDate);
            statement.setString(2, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("transaction_id");
                    int userId = resultSet.getInt("user_id");
                    String stockSymbol = resultSet.getString("stock_symbol");
                    String transactionType = resultSet.getString("transaction_type");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    String timestamp = resultSet.getString("timestamp");

                    Transaction transaction = new Transaction(transactionId, userId, stockSymbol, transactionType, quantity, price, timestamp);
                    transactions.add(transaction);
                }
            }
        }

        return transactions;
    }

    // Other methods for calculating total investment, generating reports, etc.

}