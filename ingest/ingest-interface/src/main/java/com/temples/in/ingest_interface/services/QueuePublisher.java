package com.temples.in.ingest_interface.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.temples.in.common_utils.ApplicationConfiguration;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.util.DataModelBeanConstants;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.data_model.wrapper.PrimaryKey;

@Component(value = "queuepublisher")
public class QueuePublisher implements ApplicationContextAware, IQueuePublisher {

	private static Logger LOGGER = LoggerFactory
			.getLogger(QueuePublisher.class);
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private ApplicationContext context;

	private String rabbitMqHost;
	private String exchange;
	private String routingKey;

	@Autowired
	ApplicationConfiguration configuration;

	private void readProperties() {
		LOGGER.info("Reading rabbitmq configuration properties...");
		rabbitMqHost = configuration.getRabbitMqHost();
		exchange = configuration.getRabbitMqExchange();
		routingKey = configuration.getRabbitMqRoutingKey();

		LOGGER.info("Rabbitmq configuration properties are:");
		LOGGER.info("{}={}", ApplicationConfiguration.CONFIG_RABBITMQ_HOST,
				rabbitMqHost);
		LOGGER.info("{}={}", ApplicationConfiguration.CONFIG_RABBITMQ_EXCHANGE,
				exchange);
		LOGGER.info("{}={}",
				ApplicationConfiguration.CONFIG_RABBITMQ_ROUTING_KEY,
				routingKey);
	}

	public void init() {
		readProperties();

		// Create a connection factory
		LOGGER.info("Creating new ConnectionFactory | Host={}", rabbitMqHost);
		factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		factory.setHost(rabbitMqHost);
		factory.setAutomaticRecoveryEnabled(true);

		LOGGER.info("Creating new Connection...");
		try {
			connection = factory.newConnection();

			channel = connection.createChannel();
			channel.exchangeDeclare(exchange, "direct", true);
			channel.basicQos(1);
		} catch (IOException e) {
			LOGGER.error(
					"IOException while initializing queue. Exception Message={}",
					e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"TimeoutException while initializing queue. Exception Message={}",
					e.getLocalizedMessage());
		}
	}

	public boolean putQueueMessage(String id, String message) {

		LOGGER.debug(
				"Processing | Entity Id={} | queueMessage={} | {}.putQueueMessage",
				id, message, QueuePublisher.class.getSimpleName());

		try {

			// sends message to exchange instead of queue. queues need to listen
			// to exchange to receive the message.
			LOGGER.info("Entity Id={} | Posting queue message", id);
			channel.basicPublish(exchange, routingKey,
					MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			LOGGER.info("Entity Id={} | Successfully posted queue message", id);
		} catch (IOException e) {
			LOGGER.error(
					"Entity Id={} | Error posting queue message. Error message | {} ",
					id, e.getLocalizedMessage());
			LOGGER.debug(
					"Entity Id={} | IOException while posting queue message on host {}",
					id, rabbitMqHost);
			return false;
		}

		LOGGER.debug(
				"Processed | Entity Id={} | queueMessage={} | {}.putQueueMessage",
				id, message, QueuePublisher.class.getSimpleName());
		return true;
	}

	private String getQueueMessage(Action action, EntityType entity,
			Map<String, Object> pkList) {

		PrimaryKey primaryKey = (PrimaryKey) context
				.getBean(DataModelBeanConstants.PRIMARY_KEY);
		primaryKey.setPrimaryKeys(pkList);

		EntityInfo entityInfo = (EntityInfo) context
				.getBean(DataModelBeanConstants.ENTITY_INFO);
		entityInfo.setAction(action);
		entityInfo.setEntityType(entity);
		entityInfo.setPrimaryKey(primaryKey);

		return Conversions.getJsonFromEntity(entityInfo);
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

	@Override
	public boolean enqueue(Action action, EntityType entity,
			Map<String, Object> pkList) {
		LOGGER.debug("Processing | Entity Id={} | {}.enqueue",
				pkList.get(DBConstants.ID).toString(),
				QueuePublisher.class.getSimpleName());
		String queueMessage = getQueueMessage(action, entity, pkList);
		boolean bSuccess = putQueueMessage(pkList.get(DBConstants.ID)
				.toString(), queueMessage);
		LOGGER.debug("Processed | Entity Id={} | {}.enqueue",
				pkList.get(DBConstants.ID).toString(),
				QueuePublisher.class.getSimpleName());
		return bSuccess;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}
}
