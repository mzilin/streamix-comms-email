package com.mariuszilinskas.streamix.comms.email.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(
        String accessKey,
        String secretKey,
        Ses ses
) {

    public record Ses(
            String region
    ) {}
}
