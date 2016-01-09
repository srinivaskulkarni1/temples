package com.temples.in.queue_processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.temples.in.consume_util.BeanConstants;

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
		LOGGER = LoggerFactory.getLogger(QueueProcessor.class);
		LOGGER.info("Reading configuration file...");
		QUEUE_NAME = Configuration.getProperty(Configuration.QUEUE_NAME);
		QUEUE_HOST = Configuration.getProperty(Configuration.QUEUE_HOST);
		EXCHANGE_NAME = Configuration
				.getProperty(Configuration.INGEST_EXCHANGE);
		try {
			NUM_CONSUMERS = Integer.valueOf(Configuration
					.getProperty(Configuration.NUM_CONSUMERS));
		} catch (NumberFormatException e) {
			LOGGER.warn(
					"Invalid value for configuration property {}. Defaulting to MAX_CONSUMERS({})",
					Configuration.NUM_CONSUMERS, DEFAULT_CONSUMERS);
			NUM_CONSUMERS = DEFAULT_CONSUMERS;
		}
		LOGGER.info("Configuration values are:");
		LOGGER.info("queue.name={}", QUEUE_NAME);
		LOGGER.info("queue.host={}", QUEUE_HOST);
		LOGGER.info("queue.exchange={}", EXCHANGE_NAME);
		LOGGER.info("queue.consumers={}", NUM_CONSUMERS);
	}

	public QueueProcessor() throws IOException, TimeoutException {

		LOGGER.debug("Initializing | {}", QueueProcessor.class.getName());
		try {
			context = new ClassPathXmlApplicationContext(
					BeanConstants.QUEUE_PROCSSOR_BEAN_FILE);
		} catch (BeansException e) {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Failed to load conext | Exception Message={}", e.getMessage());
		}
		context.registerShutdownHook();

		readProperties();

		// Create a connection factory
		LOGGER.info("Creating new ConnectionFactory | Host={}", QUEUE_HOST);
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(QUEUE_HOST);
		factory.setAutomaticRecoveryEnabled(true);

		// getting a connection
		LOGGER.info("Creating new Connection...");
		connection = factory.newConnection();
	}

	public void close() throws IOException, TimeoutException {
		this.connection.close();
	}

	private boolean process() {

		LOGGER.info("Creating {} queue consumers...", NUM_CONSUMERS);

		List<QueueMessageConsumer> consumerList = new ArrayList<QueueMessageConsumer>();

		for (int consumerId = 0; consumerId < NUM_CONSUMERS; consumerId++) {

			LOGGER.debug(
					"Creating Consumer({}) with values | exchange={} | queue={} | routingKey={}",
					consumerId, EXCHANGE_NAME, QUEUE_NAME, INGEST_ROUTING_KEY);
			try {
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
				channel.queueDeclare(QUEUE_NAME, true, false, false, null);
				channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, INGEST_ROUTING_KEY);
				channel.basicQos(1);
				QueueMessageConsumer queueMessageConsumer = getQueueMessageConsumer(
						channel, consumerId);
				if (queueMessageConsumer != null) {
					consumerList.add(queueMessageConsumer);
				}
			} catch (IOException e) {
				LOGGER.error(
						"IOException occured while creating consumer {} | Exception Message={}",
						consumerId, e.getLocalizedMessage());
				continue;
			}

		}

		if (consumerList.size() < 1) {
			LOGGER.error(LogConstants.MARKER_FATAL, "Failed to create queue consumers. Application will exit now...");
			return false;

		}

		LOGGER.info("Created {} queue consumers...", consumerList.size());
		LOGGER.info("Starting {} queue consumers...", consumerList.size());

		threadExecutor = Executors.newFixedThreadPool(consumerList.size());

		for (QueueMessageConsumer queueMessageConsumer : consumerList) {
			try {
				threadExecutor.submit(queueMessageConsumer);
			} catch (RejectedExecutionException e) {
				LOGGER.error(
						"RejectedExecutionException occured while starting consumer {} | Exception Message={}",
						queueMessageConsumer.getId(), e.getLocalizedMessage());
				continue;
			}
		}

		return true;

	}

	private QueueMessageConsumer getQueueMessageConsumer(Channel channel, int id) {
		try {
			QueueMessageConsumer consumer = (QueueMessageConsumer) context
					.getBean(BeanConstants.QUEUE_MESSAGE_CONSUMER);
			consumer.setChannel(channel);
			consumer.setId(id);
			consumer.setQueueName(QUEUE_NAME);
			return consumer;
		} catch (BeansException e) {
			LOGGER.error(
					"BeansException occured while creating consumer {} | Exception Message={}",
					id, e.getLocalizedMessage());
			return null;
		}
	}

	public static void main(String[] args) {
		LOGGER.info("****** STARTING CONSUME PROCESS ******");
		QueueProcessor queueProcessor = null;
		try {
			queueProcessor = new QueueProcessor();
		} catch (IOException e) {
			LOGGER.error(
					LogConstants.MARKER_FATAL,
					"IOException occured while aquiring new connection | Host={} | Exception Message={}",
					Configuration.getProperty(Configuration.QUEUE_HOST),
					e.getLocalizedMessage());
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Application will exit now...");
			System.exit(0);
		} catch (TimeoutException e) {
			LOGGER.error(
					LogConstants.MARKER_FATAL,
					"TimeoutException occured while aquiring new connection | Host={} |Exception Message={}",
					Configuration.getProperty(Configuration.QUEUE_HOST),
					e.getLocalizedMessage());
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Application will exit now...");
			System.exit(0);
		}

		boolean bStarted = queueProcessor.process();
		if (!bStarted) {
			System.exit(0);
		}

		LOGGER.info("****** CONSUME PROCESS STARTED SUCCESSFULLY ******");
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	}

}
