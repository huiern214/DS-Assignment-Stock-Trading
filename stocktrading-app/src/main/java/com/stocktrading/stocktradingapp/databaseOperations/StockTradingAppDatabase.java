package com.stocktrading.stocktradingapp.databaseOperations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class StockTradingAppDatabase {

    private Connection connection;

    public StockTradingAppDatabase(Connection connection) {
        this.connection = connection;
    }

    public void makeAllTables(StockTradingAppDatabase database) throws SQLException {
        createStocksTable();
        createTransactionsTable();
        createWatchlistTable();
        createUsersTable();
        createPortfolioTable();
        // createVirtualFundsTable();
    }

    public void createUsersTable() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "email TEXT NOT NULL,"
                + "password TEXT NOT NULL"
                + ")";

        executeUpdateQuery(createUsersTable);
    }

    // public void createVirtualFundsTable() throws SQLException {
    // String createVirtualFundsTable = "CREATE TABLE IF NOT EXISTS VirtualFunds ("
    // + "user_id INTEGER PRIMARY KEY,"
    // + "available_balance DECIMAL(10, 2) NOT NULL"
    // + ")";
    //
    // executeUpdateQuery(createVirtualFundsTable);
    // }

    public void createStocksTable() throws SQLException {
        String createStocksTable = "CREATE TABLE IF NOT EXISTS Stocks ("
                + "stock_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "company_name TEXT NOT NULL,"
                + "stock_symbol TEXT NOT NULL,"
                + "current_price DECIMAL(10, 2) NOT NULL"
                + ")";

        executeUpdateQuery(createStocksTable);
    }

    public void createTransactionsTable() throws SQLException {
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS Transactions ("
                + "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"
                + "stock_id INTEGER,"
                + "transaction_type TEXT NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "price DECIMAL(10, 2) NOT NULL,"
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        executeUpdateQuery(createTransactionsTable);
    }

    public void createWatchlistTable() throws SQLException {
        String createWatchlistTable = "CREATE TABLE IF NOT EXISTS Watchlist ("
                + "user_id INTEGER,"
                + "stock_id INTEGER,"
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        executeUpdateQuery(createWatchlistTable);
    }

    public void createPortfolioTable() throws SQLException {
        String createPortfolioTable = "CREATE TABLE IF NOT EXISTS Portfolio (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    user_id INTEGER NOT NULL," +
                "    stock_id INTEGER NOT NULL," +
                "    quantity INTEGER NOT NULL," +
                "    purchase_price REAL NOT NULL," +
                "    FOREIGN KEY (user_id) REFERENCES Users(id)," +
                "    FOREIGN KEY (stock_id) REFERENCES Stocks(id)" +
                ");";

        executeUpdateQuery(createPortfolioTable);
    }

    private void executeUpdateQuery(String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    // try { database.createPortfolioTable(); } catch (SQLException e) { throw new
    // RuntimeException(e); }
    // try { database.createStocksTable(); } catch (SQLException e) { throw new
    // RuntimeException(e); }
    // try { database.createTransactionsTable(); } catch (SQLException e) { throw
    // new RuntimeException(e); }
    // try {database.createUsersTable();} catch (SQLException e) {throw new
    // RuntimeException(e);}
    // try { database.createWatchlistTable(); } catch (SQLException e) { throw new
    // RuntimeException(e); }
    // try { database.createVirtualFundsTable(); } catch (SQLException e) { throw
    // new RuntimeException(e); }

}