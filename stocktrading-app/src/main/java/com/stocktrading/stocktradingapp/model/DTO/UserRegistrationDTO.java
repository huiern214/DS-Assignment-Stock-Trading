package com.stocktrading.stocktradingapp.model.DTO;

public class UserRegistrationDTO {
    private String username;
    private String email;
    private String password;

    public UserRegistrationDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    // Additional fields or DTO-specific logic if needed
}

