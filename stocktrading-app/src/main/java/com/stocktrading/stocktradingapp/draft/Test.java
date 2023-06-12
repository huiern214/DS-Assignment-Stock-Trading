package com.stocktrading.stocktradingapp.draft;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

enum OrderType {
    BUY, SELL
}

enum OrderStatus {
    PENDING, SUCCESS, FAILURE
}

class Account {
    String id;
    int sharesOwned;
    double accountBalance;

    public Account(String id, double accountBalance) {
        this.id = id;
        this.accountBalance = accountBalance;
    }
}

class Stock {
    String code;
    String name;
    double currentMarketPrice;

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public double getCurrentMarketPriceFromAPI() {
        String apiURL = "9c35e1454a327f73a03749c97294327f51b850c3d684a19f131c5724a93cb34e" + this.code;

        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = in.readLine()) != null) {
                content.append(line);
            }

            // Close connections
            in.close();
            conn.disconnect();

            // Assuming the response is a plain text representing the stock price
            this.currentMarketPrice = Double.parseDouble(content.toString());
            return this.currentMarketPrice;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1.0;
    }
}

class Order {
    Account account;
    Stock stock;
    int quantity;
    double price;
    OrderType orderType;
    OrderStatus orderStatus = OrderStatus.PENDING;

    public Order(Account account, Stock stock, int quantity, double price, OrderType orderType) {
        this.account = account;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
    }
}

public class Test {
    private Map<String, Stock> stockMap = new HashMap<>();
    private List<Account> accounts = new ArrayList<>();
    private List<Order> orderBook = new ArrayList<>();
    private int tradingDays = 0;//calculate the days

    public static void main(String[] args) {
        Test warriors = new Test();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to Wall Street Warriors!");
            System.out.println("1. Place an order");
            System.out.println("2. Start a new trading day");
            System.out.println("3. Generate account report");
            System.out.println("4. View owned stocks");
            System.out.println("5. View all stocks");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    System.out.print("Enter account ID: ");
                    String id = scanner.nextLine();
                    double balance = 50000.0;  // fixed initial balance
                    warriors.addAccount(new Account(id, balance));
                    break;

               case 2:
                    System.out.print("Enter stock code: ");
                    String code = scanner.nextLine();
                    System.out.print("Enter stock name: ");
                    String name = scanner.nextLine();
                    Stock newStock = new Stock(code, name);
                    double price = newStock.getCurrentMarketPriceFromAPI();  // get initial price from API
                    warriors.addStock(code, name, price);
                break;

                // ... Other cases ...
            }
        }
    }
        

    

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void addStock(String code, String name, double initialPrice) {
        Stock stock = new Stock(code, name);
        stock.currentMarketPrice = initialPrice;
        stockMap.put(code, stock);
    }

    public void placeOrder(Order order) {
        // add order to order book
        orderBook.add(order);
        // match orders
        matchOrders();
    }

    public void startNewTradingDay() {
        tradingDays++;
    }

    private void matchOrders() {
        for (int i = 0; i < orderBook.size(); i++) {
            for (int j = i+1; j < orderBook.size(); j++) {
                Order order1 = orderBook.get(i);
                Order order2 = orderBook.get(j);
                if (order1.stock.code.equals(order2.stock.code) &&
                    order1.price == order2.price) {
                    executeTrade(order1, order2);
                }
            }
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder) {
        // update buyer's and seller's account balances and share ownership
        buyOrder.account.accountBalance -= buyOrder.price * buyOrder.quantity;
        sellOrder.account.accountBalance += sellOrder.price * sellOrder.quantity;
buyOrder.account.sharesOwned += buyOrder.quantity;
sellOrder.account.sharesOwned -= sellOrder.quantity;

// Update order status
buyOrder.orderStatus = OrderStatus.SUCCESS;
sellOrder.orderStatus = OrderStatus.SUCCESS;

// Update the market price of the stock
buyOrder.stock.currentMarketPrice = buyOrder.price;
    }

    public void generateReport(String accountId) {
        for (Account account : accounts) {
            if (account.id.equals(accountId)) {
                double accountBalance = account.accountBalance;
                int sharesOwned = account.sharesOwned;

                System.out.println("Account ID: " + accountId);
                System.out.println("Account Balance: " + accountBalance);
                System.out.println("Shares Owned: " + sharesOwned);
            }
        }
    }
}