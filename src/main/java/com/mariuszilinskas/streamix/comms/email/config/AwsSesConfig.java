package com.mariuszilinskas.streamix.comms.email.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.mariuszilinskas.streamix.comms.email.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsSesConfig {

    private final AwsProperties awsProperties;

    @Bean
    public AmazonSimpleEmailService sesClient() {
        AWSCredentials credentials = new BasicAWSCredentials(awsProperties.accessKey(), awsProperties.secretKey());
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsProperties.ses().region())
                .build();
    }

}
