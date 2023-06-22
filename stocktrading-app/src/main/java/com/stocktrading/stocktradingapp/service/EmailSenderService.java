package com.stocktrading.stocktradingapp.service;

// import javax.mail.Authenticator;
// import javax.mail.Message;
// import javax.mail.MessagingException;
// import javax.mail.PasswordAuthentication;
// import javax.mail.Session;
// import javax.mail.Transport;
// import javax.mail.*;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;
import java.util.Properties;

@Service
public class EmailSenderService {
    private final UserService userService;

    @Value("${email.username}")
    private String EMAIL_USERNAME;

    @Value("${email.password}")
    private String EMAIL_PASSWORD;

    public EmailSenderService(UserService userService) {
        this.userService = userService;
    }

    public void sendSimpleEmail(int userId, String subject, String body) throws SQLException {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                    }
                });

        String userEmail = userService.getUserEmail(userId);

        if (userEmail != null) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_USERNAME));
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
