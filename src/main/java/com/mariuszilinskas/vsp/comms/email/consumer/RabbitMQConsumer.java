package com.mariuszilinskas.vsp.comms.email.consumer;

import com.mariuszilinskas.vsp.comms.email.dto.EmailRequest;
import com.mariuszilinskas.vsp.comms.email.dto.ResetPasswordRequest;
import com.mariuszilinskas.vsp.comms.email.dto.VerifyEmailRequest;
import com.mariuszilinskas.vsp.comms.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final EmailService emailService;

    @RabbitListener(queues = "${rabbitmq.queues.platform-emails}")
    public void consumeEmailMessages(EmailRequest request) {
        switch (request.getType()) {
            case "verify" -> emailService.sendVerifyAccountEmail((VerifyEmailRequest) request);
            case "welcome" -> emailService.sendWelcomeEmail(request);
            case "reset" -> emailService.sendResetPasswordEmail((ResetPasswordRequest) request);
            default -> logger.error("Unsupported Email Request type: {}", request.getType());
        }
    }

}
