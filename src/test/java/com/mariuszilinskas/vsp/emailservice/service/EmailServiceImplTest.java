package com.mariuszilinskas.vsp.emailservice.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.mariuszilinskas.vsp.emailservice.dto.EmailRequest;
import com.mariuszilinskas.vsp.emailservice.dto.ResetPasswordRequest;
import com.mariuszilinskas.vsp.emailservice.dto.VerifyEmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private AmazonSimpleEmailService sesClient;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    // ------------------------------------

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        // Setting values for @Value fields
        setField(emailService, "fromEmail", "noreply@example.com");
        setField(emailService, "baseUrl", "https://website.com");
    }

    // Utility method to set value for @Value annotated private fields
    private void setField(Object service, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = service.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
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
