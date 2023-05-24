# WIA1002 Data Structure Group Assignment
Topic 5: Wall Street Warriors  
Occurrence: 5 (Group 6)

## Project Description
This project is a stock trading application that fetches real-time stock data using Yahoo Finance API and provides a user-friendly interface for users to view and trade stocks. It utilizes data structures and algorithms to efficiently manage the stock listing, prioritize orders, and implement trading rules.

## Project Structure

The project follows a standard directory structure that helps organize the source code and resources. Here's an overview of the project structure:

```
stocktrading-app
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── stocktrading
│   │   │           └── stocktradingapp
│   │   │               ├── controller
│   │   │               ├── model
│   │   │               ├── service
│   │   │               └── StockTradingApplication.java
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       └── application.properties
│   │           
│   └── test
│       └── java
│           └── com
│               └── stocktrading
│                   └── stocktradingapp
│                       └── StockTradingApplicationTests.java
├── pom.xml
└── .gitignore
```

- `src`: Contains the main source code and resources of the project.

  - `main`: Directory for the main source code.

    - `java`: Directory for the Java source code.

      - `com.stocktrading.stocktradingapp`: Package directory that follows the Java package naming convention.

        - `controller`: Contains the controllers responsible for handling HTTP requests and defining API endpoints.

        - `model`: Contains the model classes that represent the entities in the application, such as `Stock`.

        - `service`: Contains the service classes that implement the business logic and interact with external APIs.

        - `StockTradingApplication.java`: Main entry point of the application.

    - `resources`: Directory for additional resources used by the application.

      - `static`: Directory for storing static files such as CSS, JavaScript, and images.

      - `templates`: Directory for storing templates used by the application.

      - `application.properties`: Configuration file for the application.

- `test`: Contains the test code for the project.

  - `java`: Directory for test code written in Java.

    - `com.stocktrading.stocktradingapp`: Package directory that mirrors the main source code structure.

      - `StockTradingApplicationTests.java`: Test class(es) for testing the application's functionality.

- `pom.xml`: Maven Project Object Model file that defines project dependencies, build configuration, and plugins.

- `.gitignore`: Specifies which files and directories should be ignored by version control.

## Getting Started

To run the stock trading application locally, follow these steps:

1. Obtain your own API key from https://rapidapi.com/sparior/api/yahoo-finance15.
2. Update the API key in the `StockService.java` file located at `stocktrading-app/src/main/java/com/stocktrading/stocktradingapp/service/`.
3. Run `StockTradingApplication.java`  
4. Access the application in your web browser at `http://localhost:8080`.

## Usage

- Visit `http://localhost:8080/stocks` to view the list of available stocks