# WIA1002 Data Structure Group Assignment
Topic 5:  Wall Street Warriors  
Occurence: 5 (Group 6)  

---
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
│   │   │               ├── sample
│   │   │               └── StockTradingApplication.java
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       ├── application.properties
│   │       └── database
│   │           └── scripts
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

        - `sample`: A sample package directory (can be removed).

        - `StockTradingApplication.java`: Main entry point of the application.

    - `resources`: Directory for additional resources used by the application.

      - `static`: Directory for storing static files such as CSS, JavaScript, and images.

      - `templates`: Directory for storing templates used by the application.

      - `application.properties`: Configuration file for the application.

      - `database`: Directory for storing database-related resources.

        - `scripts`: Subdirectory for storing database scripts.

- `test`: Contains the test code for the project.

  - `java`: Directory for test code written in Java.

    - `com.stocktrading.stocktradingapp`: Package directory that mirrors the main source code structure.

      - `StockTradingApplicationTests.java`: Test class(es) for testing the application's functionality.

- `pom.xml`: Maven Project Object Model file that defines project dependencies, build configuration, and plugins.

- `.gitignore`: Specifies which files and directories should be ignored by version control.
---
