package com.mariuszilinskas.streamix.comms.email.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "email")
public record EmailProperties(
        String fromEmail,
        String frontendBaseUrl
) {}
