# WIA1002 Data Structure Group Assignment
Topic 5: Wall Street Warriors  
Occurrence: 5 (Group 6)

## Project Description
This project is a stock trading application that fetches real-time stock data using Yahoo Finance API and provides a user-friendly interface for users to view and trade stocks. It utilizes data structures and algorithms to efficiently manage the stock listing, prioritize orders, and implement trading rules.

## Getting Started

To run the stock trading application locally, follow these steps:

**Server**  
1. Obtain your own API key from https://rapidapi.com/sparior/api/yahoo-finance15.
2. Update the API key in the `StockService.java` file located at `stocktrading-app/src/main/java/com/stocktrading/stocktradingapp/service/`.
3. Create an `.env` file in `stocktrading-app/src/main/resources`
  ```
    API_KEY="xxxxxxxxxxx"  
    API_BASE_URL="https://yahoo-finance15.p.rapidapi.com/api/yahoo/qu/quote/"
  ```
4. Run `StockTradingApplication.java`  
5. Access the application in your web browser at `http://localhost:8080`.

**Database**
1. View the guide [here](Installation%20for%20sqlite3.md) to install sqlite3 and setup the database.
  
**Client**  
1. Install [Node.js]("https://nodejs.org/en/download")  
2. Open terminal
  ```
    cd client
    cd stock-trading-client
    npm start
  ```
  if there is an error (below), run `npm install react-scripts --save` and try again
  ```
    'react-scripts' is not recognized as an internal or external command, operable program or batch file.
  ```
3. Access the application in your web browser at `http://localhost:3000`.  
4. Extensions for VSCode:
   - ES7+ React/Redux/React-Native snippets (ID: dsznajder.es7-react-js-snippets)  
   
## Usage

- Visit `http://localhost:8080/stocks` to view the list of available stocks
- Visit `http://localhost:3000` to view the list of available stocks and search function

## Project Structure

The project follows a standard directory structure that helps organize the source code and resources. Here's an overview of the project structure (server):

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