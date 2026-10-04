package com.mariuszilinskas.streamix.comms.email.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.mariuszilinskas.streamix.comms.email.dto.EmailRequest;
import com.mariuszilinskas.streamix.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.streamix.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.streamix.comms.email.properties.EmailProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private AmazonSimpleEmailService sesClient;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private EmailServiceImpl emailService;

    // ------------------------------------

    @BeforeEach
    void setup() {
        when(emailProperties.fromEmail()).thenReturn("noreply@example.com");
    }

    // ------------------------------------

    @Test
    void testSendVerifyAccountEmail() {
        // Arrange
        VerifyEmailRequest request = new VerifyEmailRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setPasscode("123456");

        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Mock template content");

        // Act
        emailService.sendVerifyAccountEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("verifyAccount.html"), any(Context.class));
    }

    // ------------------------------------

    @Test
    void testSendWelcomeEmail() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");

        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Mock template content");

        // Act
        emailService.sendWelcomeEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("welcome.html"), any(Context.class));
    }

    // ------------------------------------

    @Test
    void testRendResetPasswordEmail() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setResetToken("kjghke4htlk3jgt5k3");

        when(emailProperties.frontendBaseUrl()).thenReturn("https://website.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Mock template content");


        // Act
        emailService.sendResetPasswordEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("resetPassword.html"), any(Context.class));
    }

    // ------------------------------------

    @Test
    void testEmailSendingFailure() {
        // Arrange
        EmailRequest request = new EmailRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");

        doThrow(new RuntimeException("AWS SES Failure")).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act
        emailService.sendWelcomeEmail(request);
    }


}
