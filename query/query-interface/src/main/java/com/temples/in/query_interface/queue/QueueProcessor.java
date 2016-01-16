package com.temples.in.query_interface.queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.temples.in.common_utils.Configuration;
import com.temples.in.query_util.BeanConstants;

public class QueueProcessor implements ApplicationContextAware {
	private Connection connection;
	private Channel channel;
	private String QUEUE_HOST;
	private String EXCHANGE_NAME;
	private String ROUTING_KEY;
	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueProcessor.class);
	private AbstractApplicationContext context;

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

	public void close() throws IOException, TimeoutException {
		this.connection.close();
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
			QueueMessageConsumer queueMessageConsumer = getQueueMessageConsumer(
					channel, queueName);
			queueMessageConsumer.init();
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

	private QueueMessageConsumer getQueueMessageConsumer(Channel channel,
			String queueName) {
		try {
			QueueMessageConsumer consumer = (QueueMessageConsumer) context
					.getBean(BeanConstants.QUEUE_MESSAGE_CONSUMER);
			consumer.setChannel(channel);
			consumer.setQueueName(queueName);
			return consumer;
		} catch (BeansException e) {
			LOGGER.error(
					"BeansException occured while creating consumer {} | Exception Message={}",
					queueName, e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
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

	/*
	 * public static void main(String[] args) {
	 * LOGGER.info("****** STARTING CONSUME PROCESS ******"); QueueProcessor
	 * queueProcessor = null; try { queueProcessor = new QueueProcessor(); }
	 * catch (IOException e) { LOGGER.error( LogConstants.MARKER_FATAL,
	 * "IOException occured while aquiring new connection | Host={} | Exception Message={}"
	 * , Configuration.getProperty(Configuration.QUEUE_HOST),
	 * e.getLocalizedMessage()); LOGGER.error(LogConstants.MARKER_FATAL,
	 * "Application will exit now..."); System.exit(0); } catch
	 * (TimeoutException e) { LOGGER.error( LogConstants.MARKER_FATAL,
	 * "TimeoutException occured while aquiring new connection | Host={} |Exception Message={}"
	 * , Configuration.getProperty(Configuration.QUEUE_HOST),
	 * e.getLocalizedMessage()); LOGGER.error(LogConstants.MARKER_FATAL,
	 * "Application will exit now..."); System.exit(0); }
	 * 
	 * boolean bStarted = queueProcessor.init(); if (!bStarted) {
	 * System.exit(0); }
	 * 
	 * LOGGER.info("****** CONSUME PROCESS STARTED SUCCESSFULLY ******");
	 * System.out.println(" [*] Waiting for messages. To exit press CTRL+C"); }
	 */

}
