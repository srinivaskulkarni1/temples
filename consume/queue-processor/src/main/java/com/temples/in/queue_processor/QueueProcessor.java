package com.temples.in.queue_processor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.temples.in.common_utils.Configuration;
import com.temples.in.common_utils.LogConstants;

public class QueueProcessor {
	private Connection connection;
	private String QUEUE_NAME;
	private String QUEUE_HOST;
	private String EXCHANGE_NAME;
	private final String INGEST_ROUTING_KEY = "INGEST_ROUTING_KEY";
	private ExecutorService threadExecutor;
	private Integer NUM_CONSUMERS;
	private Integer DEFAULT_CONSUMERS = 5;
	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueProcessor.class);
	private AbstractApplicationContext context;

	private void readProperties() {
		LOGGER = LoggerFactory
				.getLogger(QueueProcessor.class);
		LOGGER.info("Reading configuration file...");
		QUEUE_NAME = Configuration.getProperty(Configuration.QUEUE_NAME);
		QUEUE_HOST = Configuration.getProperty(Configuration.QUEUE_HOST);
		EXCHANGE_NAME = Configuration
				.getProperty(Configuration.INGEST_EXCHANGE);
		try {
			NUM_CONSUMERS = Integer.valueOf(Configuration
					.getProperty(Configuration.NUM_CONSUMERS));
		} catch (NumberFormatException e) {
			LOGGER.error(
					"Invalid value for configuration property {}. Defaulting to MAX_CONSUMERS({})",
					Configuration.NUM_CONSUMERS, DEFAULT_CONSUMERS);
			NUM_CONSUMERS = DEFAULT_CONSUMERS;
		}
		LOGGER.debug("Configuration values...");
		LOGGER.debug("queue.name | {}", QUEUE_NAME);
		LOGGER.debug("queue.host | {}", QUEUE_HOST);
		LOGGER.debug("queue.exchange | {}", EXCHANGE_NAME);
		LOGGER.debug("queue.consumers | {}", NUM_CONSUMERS);
	}

	public QueueProcessor() throws IOException, TimeoutException {

		LOGGER.debug("Initializing | {}", QueueProcessor.class.getName());
		try {
			context = new ClassPathXmlApplicationContext(
					"query-processor-beans.xml");
		} catch (BeansException e) {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Failed to load conext | {}", e.getMessage());
		}
		context.registerShutdownHook();

		readProperties();

		// Create a connection factory
		LOGGER.debug("Creating new ConnectionFactory on host {}", QUEUE_HOST);
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(QUEUE_HOST);
		factory.setAutomaticRecoveryEnabled(true);

		// getting a connection
		LOGGER.debug("Creating new Connection...");
		connection = factory.newConnection();
	}

	public void close() throws IOException, TimeoutException {
		this.connection.close();
	}

	private void process() {

		LOGGER.info("Creating {} queue consumers...", NUM_CONSUMERS);
		try {
			threadExecutor = Executors.newFixedThreadPool(NUM_CONSUMERS);
		} catch (IllegalArgumentException e) {
			LOGGER.error(
					"Invalid value for configuration property {}. Exception message is | {}",
					Configuration.NUM_CONSUMERS, e.getLocalizedMessage());
		}

		for (int consumer = 0; consumer < NUM_CONSUMERS; consumer++) {

			LOGGER.debug(
					"Creating consumer({}) with values | exchange({}) | queue({}) | routingKey({})",
					consumer, EXCHANGE_NAME, QUEUE_NAME, INGEST_ROUTING_KEY);
			try {
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
				channel.queueDeclare(QUEUE_NAME, true, false, false, null);
				channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, INGEST_ROUTING_KEY);
				channel.basicQos(1);
				QueueMessageConsumer queueMessageConsumer = new QueueMessageConsumer(
						channel, QUEUE_NAME, consumer);
				threadExecutor.submit(queueMessageConsumer);

			} catch (IOException e) {
				LOGGER.error(
						"IOException occured while creating consumer {}. Exception message is | {}",
						consumer, e.getLocalizedMessage());
			} catch (RejectedExecutionException e) {
				LOGGER.error(
						"RejectedExecutionException occured while creating consumer {}. Exception message is | {}",
						consumer, e.getLocalizedMessage());
			} catch (NullPointerException e) {
				LOGGER.error(
						"NullPointerException occured while creating consumer {}. Exception message is | {}",
						consumer, e.getLocalizedMessage());
			}

		}
	}

	public static void main(String[] args) {
		try {
			QueueProcessor queueProcessor = new QueueProcessor();
			queueProcessor.process();
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured while aquiring new connection on host {}. Exception message is | {}",
					Configuration.getProperty(Configuration.QUEUE_HOST), e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"TimeoutException occured while aquiring new connection on host {}. Exception message is | {}",
					Configuration.getProperty(Configuration.QUEUE_HOST), e.getLocalizedMessage());
		}
	}

}
