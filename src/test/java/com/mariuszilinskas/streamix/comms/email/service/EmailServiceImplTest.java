package com.mariuszilinskas.streamix.comms.email.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.AmazonSimpleEmailServiceException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.mariuszilinskas.streamix.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.streamix.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.streamix.comms.email.dto.WelcomeEmailRequest;
import com.mariuszilinskas.streamix.comms.email.exception.EmailSendException;
import com.mariuszilinskas.streamix.comms.email.model.EmailLog;
import com.mariuszilinskas.streamix.comms.email.model.EmailLogStatus;
import com.mariuszilinskas.streamix.comms.email.properties.EmailProperties;
import com.mariuszilinskas.streamix.comms.email.repository.EmailLogRepository;
import com.mariuszilinskas.streamix.cryptography.service.FieldEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private AmazonSimpleEmailService sesClient;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private EmailProperties emailProperties;

    @Mock
    private EmailLogRepository emailLogRepository;

    @Mock
    private FieldEncryptionService fieldEncryptionService;

    @InjectMocks
    private EmailServiceImpl emailService;

    private static final UUID USER_ID = UUID.randomUUID();

    // ------------------------------------

    @BeforeEach
    void setup() {
        when(emailProperties.fromEmail()).thenReturn("noreply@example.com");
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("Mock template content");
        when(fieldEncryptionService.decrypt(anyString())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ------------------------------------

    @Test
    void sendVerifyAccountEmail_sendsEmailAndRendersVerificationTemplate() {
        // Arrange
        VerifyEmailRequest request = new VerifyEmailRequest();
        request.setUserId(USER_ID);
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
        verify(emailLogRepository, times(1)).save(any(EmailLog.class));
    }

    @Test
    void sendVerifyAccountEmail_includesVerificationUrlInTemplateContext() {
        // Arrange
        VerifyEmailRequest request = new VerifyEmailRequest();
        request.setUserId(USER_ID);
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
        WelcomeEmailRequest request = new WelcomeEmailRequest(USER_ID, "Test User", "test@example.com");
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(new SendEmailResult());

        // Act
        emailService.sendWelcomeEmail(request);

        // Assert
        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
        verify(templateEngine, times(1)).process(eq("welcome.html"), any(Context.class));
        verify(emailLogRepository, times(1)).save(any(EmailLog.class));
    }

    @Test
    void sendWelcomeEmail_whenSesFails_throwsEmailSendException() {
        // Arrange
        WelcomeEmailRequest request = new WelcomeEmailRequest(USER_ID, "Test User", "test@example.com");
        AmazonSimpleEmailServiceException sesFailure = new AmazonSimpleEmailServiceException("AWS SES Failure");

        doThrow(sesFailure).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act & Assert
        EmailSendException exception = assertThrows(
                EmailSendException.class,
                () -> emailService.sendWelcomeEmail(request)
        );

        assertTrue(exception.getMessage().contains("welcome"));
        assertEquals(sesFailure, exception.getCause());

        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void sendWelcomeEmail_whenSesFails_persistsFailedLog() {
        // Arrange
        WelcomeEmailRequest request = new WelcomeEmailRequest(USER_ID, "Test User", "test@example.com");
        doThrow(new AmazonSimpleEmailServiceException("AWS SES Failure")).when(sesClient).sendEmail(any(SendEmailRequest.class));

        // Act
        assertThrows(EmailSendException.class, () -> emailService.sendWelcomeEmail(request));

        // Assert
        ArgumentCaptor<EmailLog> logCaptor = ArgumentCaptor.forClass(EmailLog.class);
        verify(emailLogRepository, times(1)).save(logCaptor.capture());

        EmailLog savedLog = logCaptor.getValue();
        assertEquals(USER_ID, savedLog.getUserId());
        assertEquals("welcome", savedLog.getType());
        assertEquals(EmailLogStatus.FAILED, savedLog.getStatus());
        assertNotNull(savedLog.getCreatedAt());}

    // ------------------------------------

    @Test
    void sendResetPasswordEmail_sendsEmailAndRendersResetTemplate() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setUserId(USER_ID);
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
        verify(emailLogRepository, times(1)).save(any(EmailLog.class));
    }

    @Test
    void sendResetPasswordEmail_includesResetLinkInTemplateContext() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setUserId(USER_ID);
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
