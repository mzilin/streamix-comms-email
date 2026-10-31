package com.mariuszilinskas.streamix.comms.email.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.mariuszilinskas.streamix.comms.email.dto.EmailRequest;
import com.mariuszilinskas.streamix.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.streamix.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.streamix.comms.email.exception.EmailSendException;
import com.mariuszilinskas.streamix.comms.email.model.EmailLog;
import com.mariuszilinskas.streamix.comms.email.model.EmailLogStatus;
import com.mariuszilinskas.streamix.comms.email.properties.EmailProperties;
import com.mariuszilinskas.streamix.comms.email.repository.EmailLogRepository;
import com.mariuszilinskas.streamix.cryptography.service.FieldEncryptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

/**
 * Service implementation for managing Platform Transactional emails.
 *
 * @author Marius Zilinskas
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final AmazonSimpleEmailService sesClient;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;
    private final EmailLogRepository emailLogRepository;
    private final FieldEncryptionService fieldEncryptionService;

    @Override
    public void sendVerifyAccountEmail(VerifyEmailRequest request) {
        decryptRequest(request);
        ModelMap model = initialiseModelMap(request);
        String verificationUrl = emailProperties.frontendBaseUrl() + "/verify?token=" + request.getVerificationToken();
        model.addAttribute("verificationUrl", verificationUrl);

        String body = buildEmail("verifyAccount.html", model);
        sendEmail(request, "Verify Your Account", body);
    }

    @Override
    public void sendWelcomeEmail(EmailRequest request) {
        decryptRequest(request);
        ModelMap model = initialiseModelMap(request);
        String body = buildEmail("welcome.html", model);
        sendEmail(request, "Welcome to Streamix!", body);
    }

    @Override
    public void sendResetPasswordEmail(ResetPasswordRequest request) {
        decryptRequest(request);
        ModelMap model = initialiseModelMap(request);
        String resetLink = emailProperties.frontendBaseUrl() + "/reset-password?token=" + request.getResetToken();
        model.addAttribute("resetLink", resetLink);

        String body = buildEmail("resetPassword.html", model);
        sendEmail(request, "Reset Your Password", body);
    }

    private void decryptRequest(EmailRequest request) {
        request.setFirstName(fieldEncryptionService.decrypt(request.getFirstName()));
        request.setEmail(fieldEncryptionService.decrypt(request.getEmail()));
    }

    private ModelMap initialiseModelMap(EmailRequest request) {
        ModelMap model = new ModelMap();
        model.addAttribute("firstName", request.getFirstName());
        model.addAttribute("email", request.getEmail());
        return model;
    }

    private String buildEmail(String templateName, ModelMap model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName, context);
    }

    private void sendEmail(EmailRequest request, String subject, String body) {
        String fromFormatted = "Streamix <" + emailProperties.fromEmail() + ">";

        Destination destination = new Destination().withToAddresses(request.getEmail());
        Content subjectContent = new Content().withCharset("UTF-8").withData(subject);
        Content htmlBodyContent = new Content().withCharset("UTF-8").withData(body);

        Message message = new Message()
                .withBody(new Body().withHtml(htmlBodyContent))
                .withSubject(subjectContent);

        SendEmailRequest sendEmailRequest = new SendEmailRequest()
                .withDestination(destination)
                .withMessage(message)
                .withSource(fromFormatted);

        UUID userId = request.getUserId();
        String type = request.getType();

        try {
            sesClient.sendEmail(sendEmailRequest);
            logger.info("{} email sent successfully [userId: {}]", type, userId);
            persistLog(request, EmailLogStatus.SENT);
        } catch (AmazonSimpleEmailServiceException ex) {
            logger.error("{} email rejected by SES for userId={}", type, userId, ex);
            persistLog(request, EmailLogStatus.FAILED);
            throw new EmailSendException("Failed to send " + type + " email", ex);
        }
    }

    private void persistLog(EmailRequest request, EmailLogStatus status) {
        EmailLog log = new EmailLog(
                request.getUserId(),
                request.getType(),
                status
        );
        emailLogRepository.save(log);
    }

}