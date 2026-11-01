package com.mariuszilinskas.streamix.comms.email.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.mariuszilinskas.streamix.comms.email.dto.EmailRequest;
import com.mariuszilinskas.streamix.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.streamix.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.streamix.comms.email.exception.EmailSendException;
import com.mariuszilinskas.streamix.comms.email.model.EmailLog;
import com.mariuszilinskas.streamix.comms.email.model.EmailLogStatus;
import com.mariuszilinskas.streamix.comms.email.model.EmailTemplate;
import com.mariuszilinskas.streamix.comms.email.properties.EmailProperties;
import com.mariuszilinskas.streamix.comms.email.repository.EmailLogRepository;
import com.mariuszilinskas.streamix.cryptography.service.FieldEncryptionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
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
        EmailDetails details = decryptRequest(request);

        String verificationUrl = UriComponentsBuilder
                .fromUriString(emailProperties.frontendBaseUrl())
                .path("/verify")
                .queryParam("token", request.getVerificationToken())
                .build()
                .encode()
                .toUriString();

        Map<String, Object> model = Map.of(
                "firstName", details.firstName(),
                "email", details.email(),
                "verificationUrl", verificationUrl
        );

        String body = buildEmail(EmailTemplate.VERIFY_ACCOUNT, model);
        sendEmail(request, details.email(), EmailTemplate.VERIFY_ACCOUNT, body);
    }

    @Override
    public void sendWelcomeEmail(EmailRequest request) {
        EmailDetails details = decryptRequest(request);

        Map<String, Object> model = Map.of(
                "firstName", request.getFirstName(),
                "email", request.getEmail()
        );

        String body = buildEmail(EmailTemplate.WELCOME, model);
        sendEmail(request, details.email(), EmailTemplate.WELCOME, body);
    }

    @Override
    public void sendResetPasswordEmail(ResetPasswordRequest request) {
        EmailDetails details = decryptRequest(request);
        String resetToken = fieldEncryptionService.decrypt((request.getResetToken()));

        String resetLink = UriComponentsBuilder
                .fromUriString(emailProperties.frontendBaseUrl())
                .path("/reset-password")
                .queryParam("token", resetToken)
                .build()
                .encode()
                .toUriString();

        Map<String, Object> model = Map.of(
                "firstName", details.firstName(),
                "email", details.email(),
                "resetLink", resetLink
        );

        String body = buildEmail(EmailTemplate.RESET_PASSWORD, model);
        sendEmail(request, details.email(), EmailTemplate.RESET_PASSWORD, body);
    }

    private EmailDetails decryptRequest(EmailRequest request) {
        return new EmailDetails(
                fieldEncryptionService.decrypt(request.getFirstName()),
                fieldEncryptionService.decrypt(request.getEmail())
        );
    }

    private String buildEmail(EmailTemplate emailTemplate, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(emailTemplate.getTemplate(), context);
    }

    private void sendEmail(EmailRequest request, String recipient, EmailTemplate emailTemplate, String body) {
        String fromFormatted = "Streamix <" + emailProperties.fromEmail() + ">";

        Destination destination = new Destination()
                .withToAddresses(recipient);

        Content subjectContent = new Content()
                .withCharset("UTF-8")
                .withData(emailTemplate.getSubject());

        Content htmlBodyContent = new Content()
                .withCharset("UTF-8")
                .withData(body);

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
        } catch (AmazonSimpleEmailServiceException ex) {
            logger.error("{} email rejected by SES [userId: {}]", type, userId, ex);

            try {
                persistLog(request, EmailLogStatus.FAILED);
            } catch (Exception loggingException) {
                logger.error(
                        "Failed to persist email failure log [userId: {}]",
                        userId,
                        loggingException
                );
            }

            throw new EmailSendException("Failed to send " + type + " email", ex);
        }

        logger.info("{} email submitted successfully [userId: {}]", type, userId);

        try {
            persistLog(request, EmailLogStatus.SENT);
        } catch (Exception ex) {
            logger.error(
                    "Email was accepted by SES but delivery log persistence failed [userId: {}]",
                    userId,
                    ex
            );
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

    private record EmailDetails(String firstName, String email) {}

}