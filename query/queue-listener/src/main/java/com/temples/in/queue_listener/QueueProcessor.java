package com.temples.in.queue_listener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.temples.in.common_utils.ApplicationConfiguration;

public class QueueProcessor {
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String rabbitMqHost;
	private String exchange;
	private String routingKey;
	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueProcessor.class);

	@Autowired
	private QueueMessageConsumer consumer;
	
	@Autowired
	ApplicationConfiguration configuration;

	private void readProperties() {
		LOGGER.info("Reading rabbitmq configuration properties...");
		rabbitMqHost = configuration.getRabbitMqHost();
		exchange = configuration.getRabbitMqExchange();
		routingKey = configuration.getRabbitMqRoutingKey();

		LOGGER.info("Rabbitmq configuration properties are:");
		LOGGER.info("{}={}", ApplicationConfiguration.CONFIG_RABBITMQ_HOST, rabbitMqHost);
		LOGGER.info("{}={}", ApplicationConfiguration.CONFIG_RABBITMQ_EXCHANGE, exchange);
		LOGGER.info("{}={}", ApplicationConfiguration.CONFIG_RABBITMQ_ROUTING_KEY, routingKey);
	}

	public boolean init() {
		readProperties();

		// Create a connection factory
		LOGGER.info("Creating new ConnectionFactory | Host={}", rabbitMqHost);
		factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(rabbitMqHost);
		factory.setAutomaticRecoveryEnabled(true);

		// getting a connection
		LOGGER.info("Creating new Connection...");
		try {
			connection = factory.newConnection();

			channel = connection.createChannel();
			channel.exchangeDeclare(exchange, "direct", true);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchange, routingKey);
			channel.basicQos(1);

			consumer.setChannel(channel);
			consumer.setQueueName(queueName);
			consumer.init();
		} catch (IOException e) {
			LOGGER.error(
					"IOException while initializing queue. Exception Message={}",
					e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"TimeoutException while initializing queue. Exception Message={}",
					e.getLocalizedMessage());
		}

		return true;

	}

	public void destroy() {
		try {
			this.channel.close();
			this.connection.close();
		} catch (IOException e) {
			LOGGER.error(
					"IOException while closing queue connection. Exception Message={}",
					e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"TimeoutException while closing queue connection. Exception Message={}",
					e.getLocalizedMessage());
		}
	}

}
