package com.mariuszilinskas.streamix.comms.email.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.mariuszilinskas.streamix.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.streamix.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.streamix.comms.email.dto.WelcomeEmailRequest;
import com.mariuszilinskas.streamix.comms.email.exception.EmailSendException;
import com.mariuszilinskas.streamix.comms.email.properties.EmailProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Mock template content");
    }

    // ------------------------------------

    @Test
    void sendVerifyAccountEmail_sendsEmailAndRendersVerificationTemplate() {
        // Arrange
        VerifyEmailRequest request = new VerifyEmailRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setVerificationToken("verification-token");

        when(emailProperties.frontendBaseUrl()).thenReturn("https://website.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());

        // Act
        emailService.sendVerifyAccountEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("verifyAccount.html"), any(Context.class));
    }

    @Test
    void sendVerifyAccountEmail_includesVerificationUrlInTemplateContext() {
        // Arrange
        VerifyEmailRequest request = new VerifyEmailRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setVerificationToken("verification-token");

        when(emailProperties.frontendBaseUrl()).thenReturn("https://website.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());

        // Act
        emailService.sendVerifyAccountEmail(request);

        // Assert
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        verify(templateEngine, times(1)).process(eq("verifyAccount.html"), contextCaptor.capture());

        assertEquals(
                "https://website.com/verify?token=verification-token",
                contextCaptor.getValue().getVariable("verificationUrl")
        );
    }

    // ------------------------------------

    @Test
    void sendWelcomeEmail_sendsEmailAndRendersWelcomeTemplate() {
        WelcomeEmailRequest request = new WelcomeEmailRequest("Test User", "test@example.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());

        // Act
        emailService.sendWelcomeEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("welcome.html"), any(Context.class));
    }

    @Test
    void sendWelcomeEmail_whenSesFails_throwsEmailSendException() {
        // Arrange
        WelcomeEmailRequest request = new WelcomeEmailRequest("Test User", "test@example.com");
        RuntimeException sesFailure = new RuntimeException("AWS SES Failure");

        doThrow(sesFailure).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act & Assert
        EmailSendException exception = assertThrows(
                EmailSendException.class,
                () -> emailService.sendWelcomeEmail(request)
        );

        assertTrue(exception.getMessage().contains("Welcome"));
        assertEquals(sesFailure, exception.getCause());

        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    // ------------------------------------

    @Test
    void sendResetPasswordEmail_sendsEmailAndRendersResetTemplate() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setResetToken("reset-token");

        when(emailProperties.frontendBaseUrl()).thenReturn("https://website.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());


        // Act
        emailService.sendResetPasswordEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("resetPassword.html"), any(Context.class));
    }

    @Test
    void sendResetPasswordEmail_includesResetLinkInTemplateContext() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setFirstName("Test User");
        request.setEmail("test@example.com");
        request.setResetToken("reset-token");

        when(emailProperties.frontendBaseUrl()).thenReturn("https://website.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());

        // Act
        emailService.sendResetPasswordEmail(request);

        // Assert
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);

        verify(templateEngine, times(1)).process(eq("resetPassword.html"), contextCaptor.capture());

        assertEquals(
                "https://website.com/reset-password?token=reset-token",
                contextCaptor.getValue().getVariable("resetLink")
        );
    }

}
