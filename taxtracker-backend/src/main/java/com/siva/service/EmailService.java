package com.siva.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendRegistrationConfirmation(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to TaxTracker \u2013 Registration Successful \ud83c\udf89");
            
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            
            String text = "Hi " + username + ",\n\n"
                    + "Welcome to TaxTracker! Your account has been created successfully.\n\n"
                    + "You're now ready to take control of your finances \u2014 track transactions, \n"
                    + "manage your Form 90C submissions, and stay on top of your tax records, \n"
                    + "all in one place.\n\n"
                    + "Account Details:\n"
                    + "- Username: " + username + "\n"
                    + "- Email: " + toEmail + "\n"
                    + "- Registered on: " + date + "\n\n"
                    + "If you didn't create this account, please ignore this email or contact \n"
                    + "our support team.\n\n"
                    + "Happy tracking!\n"
                    + "The TaxTracker Team";
                    
            message.setText(text);
            mailSender.send(message);
            log.info("Confirmation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}", toEmail, e);
        }
    }
}
