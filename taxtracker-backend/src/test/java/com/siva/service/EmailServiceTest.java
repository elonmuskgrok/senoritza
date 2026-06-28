package com.siva.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendRegistrationConfirmation_success_sendsEmail() {
        emailService.sendRegistrationConfirmation("test@test.com", "TestUser");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendRegistrationConfirmation_exceptionThrown_catchesException() {
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        // Should not throw an exception out of the method because it's caught inside
        emailService.sendRegistrationConfirmation("test@test.com", "TestUser");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
