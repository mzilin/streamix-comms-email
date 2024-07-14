package com.mariuszilinskas.vsp.emailservice;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.mariuszilinskas.vsp.emailservice.config.AwsSesConfig;
import com.mariuszilinskas.vsp.emailservice.config.RabbitMQConfig;
import com.mariuszilinskas.vsp.emailservice.consumer.RabbitMQConsumer;
import com.mariuszilinskas.vsp.emailservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the Spring application context and bean configuration in the EmailService application.
 */
@SpringBootTest
class EmailServiceApplicationTests {

	@Autowired
	private AmazonSimpleEmailService sesClient;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AwsSesConfig awsSesConfig;

	@Autowired
	private RabbitMQConfig rabbitMQConfig;

	@Autowired
	private RabbitMQConsumer rabbitMQConsumer;

	@Test
	void contextLoads() {
	}

	@Test
	void sesClientBeanLoads() {
		assertNotNull(sesClient, "AWS SES Client should have been auto-wired by Spring Context");
	}

	@Test
	void emailServiceBeanLoads() {
		assertNotNull(emailService, "Email Service should have been auto-wired by Spring Context");
	}

	@Test
	void awsSesConfigBeanLoads() {
		assertNotNull(awsSesConfig, "AWS SES Config should have been auto-wired by Spring Context");
	}

	@Test
	void rabbitMQConfigBeanLoads() {
		assertNotNull(rabbitMQConfig, "RabbitMQ Config should have been auto-wired by Spring Context");
	}

	@Test
	void rabbitMQConsumerBeanLoads() {
		assertNotNull(rabbitMQConsumer, "RabbitMQ Consumer should have been auto-wired by Spring Context");
	}

}
