package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.model.User;
import com.stocktrading.stocktradingapp.model.UserRegistrationDTO;
import com.stocktrading.stocktradingapp.service.UserService;

import java.sql.SQLException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDTO user) {
        boolean success = false;
        try {
            // Add validation for duplicate users
            if (userService.checkDuplicateEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
            }
            success = userService.addUser(user.getUsername(), user.getEmail(), user.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (success) {
            return ResponseEntity.ok("User registered successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user");
    }

    @PostMapping("/login")
    public ResponseEntity<Integer> loginUser(@RequestBody Map<String, String> loginInfo) {
        String email = loginInfo.get("email");
        String password = loginInfo.get("password");

        int userId = userService.authenticateUser(email, password);
        if (userId != -1) {
            return ResponseEntity.ok(userId);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(-1);
    }

    @GetMapping("/{user_id}")
    // @RequestMapping()
    public ResponseEntity<User> getUserProfile(@PathVariable int user_id) {
        try {
            User userProfile = userService.getUser(user_id);
            if (userProfile != null) {
                return ResponseEntity.ok(userProfile);
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // Example output:
    // {"userId":1,"username":"Ali","email":"Ali@gmail.com","password":"1234","funds":48689.0,"portfolio":{"holdings":{"AMMB Holdings Berhad (AMBANK)":5},"value":1775.0}}

}
