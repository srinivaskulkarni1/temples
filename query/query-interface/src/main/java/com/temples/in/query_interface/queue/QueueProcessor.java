package com.temples.in.query_interface.queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.temples.in.common_utils.Configuration;

public class QueueProcessor {
	private Connection connection;
	private Channel channel;
	private String QUEUE_HOST;
	private String EXCHANGE_NAME;
	private String ROUTING_KEY;
	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueProcessor.class);

	@Autowired
	private QueueMessageConsumer consumer;

	private void readProperties() {
		LOGGER = LoggerFactory.getLogger(QueueProcessor.class);
		LOGGER.info("Reading configuration file...");
		QUEUE_HOST = Configuration.getProperty(Configuration.QUEUE_HOST);
		EXCHANGE_NAME = Configuration
				.getProperty(Configuration.INGEST_EXCHANGE);
		ROUTING_KEY = Configuration
				.getProperty(Configuration.INGEST_ROUTING_KEY);

		LOGGER.info("Configuration values are:");
		LOGGER.info("queue.host={}", QUEUE_HOST);
		LOGGER.info("queue.exchange={}", EXCHANGE_NAME);
		LOGGER.info("queue.routingkey={}", ROUTING_KEY);
	}

	public QueueProcessor() {

		LOGGER.debug("Initializing | {}", QueueProcessor.class.getName());

		readProperties();

	}

	public boolean init() {
		// Create a connection factory
		LOGGER.info("Creating new ConnectionFactory | Host={}", QUEUE_HOST);
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(QUEUE_HOST);
		factory.setAutomaticRecoveryEnabled(true);

		// getting a connection
		LOGGER.info("Creating new Connection...");
		try {
			connection = factory.newConnection();

			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
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
					"IOException while close queue connection. Exception Message={}",
					e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"TimeoutException while close queue connection. Exception Message={}",
					e.getLocalizedMessage());
		}
	}

}
