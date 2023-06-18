package com.stocktrading.stocktradingapp.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Properties;

@Service
public class EmailSenderService {
    private final UserService userService;

    public EmailSenderService(UserService userService) {
        this.userService = userService;
    }

    public void sendSimpleEmail(int userId, String subject, String body) throws SQLException {

        final String username = "datastructureocc5@gmail.com";
        final String password = "cgstewmwwocrjoeb";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        String userEmail = userService.getUserEmail(userId);

        if (userEmail != null) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("datastructureocc5@gmail.com"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(userEmail)
                );
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);

                System.out.println("Mail sent...");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User email not found.");
        }
    }
}
