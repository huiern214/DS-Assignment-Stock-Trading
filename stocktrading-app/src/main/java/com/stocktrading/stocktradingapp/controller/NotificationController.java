package com.stocktrading.stocktradingapp.controller;

import com.stocktrading.stocktradingapp.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class NotificationController {
    private final EmailSenderService emailSenderService;

    @Autowired
    public NotificationController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    // http://localhost:8080/api/email/{userId}
    @PostMapping("/{userId}")
    public ResponseEntity<String> sendEmail(@PathVariable int userId, @RequestBody Map<String, String> emailContent) {
                                            // @RequestParam String subject,
                                            // @RequestParam String body) {
        try {
            emailSenderService.sendSimpleEmail(userId, emailContent.get("subject"), emailContent.get("body"));
            return ResponseEntity.ok("Email sent successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
        }
    }
}
