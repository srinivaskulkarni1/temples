package com.temples.in.ingest_resource.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.temples.in.common_utils.Configuration;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.util.DataModelBeanConstants;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.PrimaryKey;

public class QueueManager implements ApplicationContextAware, IQueueManager {

	private AbstractApplicationContext context;
	private static Logger LOGGER = LoggerFactory.getLogger(QueueManager.class);
	private static ConnectionFactory factory;
	private static Connection connection;

	private static final String INGEST_ROUTING_KEY = "INGEST_ROUTING_KEY";
	private static String QUEUE_NAME;
	private static String QUEUE_HOST;
	private static String EXCHANGE_NAME;

	static {
		QUEUE_NAME = Configuration.getProperty(Configuration.QUEUE_NAME);
		QUEUE_HOST = Configuration.getProperty(Configuration.QUEUE_HOST);
		EXCHANGE_NAME = Configuration
				.getProperty(Configuration.INGEST_EXCHANGE);
		

		factory = new ConnectionFactory();
		factory.setHost(QUEUE_HOST);
		factory.setAutomaticRecoveryEnabled(true);
		
		connect();
		

	}

	private static void connect() {
		LOGGER.info("Connecting to message queue | Host={}", QUEUE_HOST);
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			LOGGER.error(
					"Cannot open connection to message queue | Host={} | Error Message={} ",
					QUEUE_HOST, e.getLocalizedMessage());
		} catch (TimeoutException e) {
			LOGGER.error(
					"Timeout exception while connection to message queue | Host={} | Error Message={} ",
					QUEUE_HOST, e.getLocalizedMessage());
		}
	}

	public boolean putQueueMessage(String id, String message) {
		
		LOGGER.debug("Processing | Entity Id={} | queueMessage={} | {}.putQueueMessage",
				id, message, QueueManager.class.getSimpleName());
		
		try {
		
			if(connection == null){
				connect();
			}
			Channel channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			
			//sends message to exchange instead of queue. queues need to listen to exchange to receive the message.
			LOGGER.info("Posting queue message | Entity Id={}", id);
			channel.basicPublish(EXCHANGE_NAME, INGEST_ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			channel.close();
			LOGGER.info("Queue message posted successfully | Entity Id={}", id);			
		} catch (IOException e) {
			LOGGER.error("Error posting queue message. Error message | {} ",  e.getLocalizedMessage());
			LOGGER.debug("IOException while posting queue message on host {}",
					QUEUE_HOST);
			return false;
		} catch (TimeoutException e) {
			LOGGER.error("Timeout exception while posting queue message. Error message | {} ", e.getLocalizedMessage());
			LOGGER.debug(
					"TimeoutException exception Error posting queue message on host {}",
					QUEUE_HOST);
			return false;
		}

		LOGGER.debug("Processed | Entity Id={} | queueMessage={} | {}.putQueueMessage",
				id, message, QueueManager.class.getSimpleName());
		return true;
	}

	
	private String getQueueMessage(Action action, EntityType entity,
			Map<String, Object> pkList){
	
		PrimaryKey primaryKey = (PrimaryKey) context.getBean(DataModelBeanConstants.PRIMARY_KEY);
		primaryKey.setPrimaryKeys(pkList);
		
		EntityInfo entityInfo = (EntityInfo) context.getBean(DataModelBeanConstants.ENTITY_INFO);
		entityInfo.setAction(action);
		entityInfo.setEntityType(entity);
		entityInfo.setPrimaryKey(primaryKey);

		Gson gson = new Gson();
		return gson.toJson(entityInfo);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;

	}

	@Override
	public boolean enqueue(Action action, EntityType entity,
			Map<String, Object> pkList) {
		LOGGER.debug("Processing | Entity Id={} | {}.enqueue",
				pkList.get(DBConstants.ID).toString(), QueueManager.class.getSimpleName());
		String queueMessage = getQueueMessage(action, entity, pkList);
		boolean bSuccess = putQueueMessage(pkList.get(DBConstants.ID).toString(), queueMessage);
		LOGGER.debug("Processed | Entity Id={} | {}.enqueue",
				pkList.get(DBConstants.ID).toString(), QueueManager.class.getSimpleName());
		return bSuccess;		
	}
}
