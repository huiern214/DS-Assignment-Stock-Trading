package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.model.User;
import com.stocktrading.stocktradingapp.model.UserProfile;
import com.stocktrading.stocktradingapp.service.UserService;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        boolean success = false;
        try {
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
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> loginInfo) {
        boolean success = userService.authenticateUser(loginInfo.get("email"), loginInfo.get("password"));
        if (success) {
            return ResponseEntity.ok("User logged in successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String email) {
        try {
            UserProfile userProfile = userService.getUserProfile(email);
            if (userProfile != null) {
                return ResponseEntity.ok(userProfile);
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
