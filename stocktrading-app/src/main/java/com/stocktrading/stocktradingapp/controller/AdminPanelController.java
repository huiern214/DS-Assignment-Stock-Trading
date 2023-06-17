package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.service.AdminPanelService;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminPanelController {

    private final AdminPanelService adminPanelService;

    @Autowired
    public AdminPanelController(AdminPanelService adminPanelService) {
        this.adminPanelService = adminPanelService;
    }

    // http://localhost:8080/admin/add-stock
    @PostMapping("/add-stock")
    public ResponseEntity<String> addStock(@RequestBody Map<String, String> code) {
        adminPanelService.addStock(code.get("code"));
        return ResponseEntity.status(HttpStatus.CREATED).body("Stock added successfully");
    }
    // Example input:
    // {
    //     "code": "7113"
    // }

    // http://localhost:8080/admin/delete-stock
    @DeleteMapping("/delete-stock")
    public ResponseEntity<String> deleteStock(@RequestBody Map<String, String> code) {
        adminPanelService.deleteStock(code.get("code"));
        return ResponseEntity.ok("Stock deleted successfully");
    }
    // Example input:
    // {
    //     "code": "7113"
    // }

    // http://localhost:8080/admin/stock-list
    @GetMapping("/stock-list")
    public ResponseEntity<?> getStockList() {
        return ResponseEntity.ok(adminPanelService.stockList());
    }
    // Example output:
    // ["5296","1015","1066","1295","1961","3182","5225","4707","4863","5347","6888","1023","7277","6947","3034","5168","5819","1082","5235SS","2445","1155","6012","3816","5183","5681","6033","4065","8869","4197","5285"]

    // http://localhost:8080/admin/users-list
    @GetMapping("/users-list")
    public ResponseEntity<?> getUsersList() {
        try {
            return ResponseEntity.ok(adminPanelService.usersList());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch users");
        }
    }
    // Example output:
    // [{"userId":1,"username":"Ali","email":"Ali@gmail.com","password":"1234","funds":48689.0,"portfolio":{"holdings":{},"value":0.0}},{"userId":2,"username":"Lily","email":"lily@gmail.com","password":"1234","funds":50000.0,"portfolio":{"holdings":{},"value":0.0}}]

    // http://localhost:8080/admin/delete-user
    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestBody Map<String, Integer> userId) {
        try {
            boolean deleted = adminPanelService.deleteUser(userId.get("user_id"));
            if (deleted) {
                return ResponseEntity.ok("User deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }
    // Example input:
    // {
    //     "user_id": 2
    // }
}
