package com.temples.in.queue_processor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.EntityInfo;

public class QueueMessageConsumer implements Consumer, Runnable,
		ApplicationContextAware {

	private Channel channel;
	private String queueName;
	private int id;
	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueMessageConsumer.class);

	public QueueMessageConsumer() {
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public void handleConsumeOk(String consumerTag) {

	}

	@Override
	public void handleCancelOk(String consumerTag) {

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {

	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			AMQP.BasicProperties properties, byte[] body) throws IOException, JsonSyntaxException {

		String message = new String(body, "UTF-8");
		boolean bProcessed = false;
		Gson gson = new Gson();
		EntityInfo entityInfo = null;

		try {
			LOGGER.info("Consumer({}) | De-serializing Message={}", id, message);
			entityInfo = gson.fromJson(message, EntityInfo.class);
		} catch (JsonSyntaxException e) {
			LOGGER.error("Consumer({}) | Malformed Message={} | Exception Message={}", id,
					message, e.getLocalizedMessage());
			throw e;
		}
		
		String entityId = (String) entityInfo.getPrimaryKey().getPrimaryKeys().get(DBConstants.ID);
		try {
			MessageProcessor messageProcessor = (MessageProcessor) context
					.getBean(BeanConstants.MESSAGE_PROCESSOR);
			bProcessed = messageProcessor.process(id, entityId, entityInfo);
		} catch (BeansException e) {
			LOGGER.error(
					"Consumer({}) | Entity Id={} | BeansException while processing queue message | Exception Message={}",
					id, entityId, e.getLocalizedMessage());
			LOGGER.debug(
					"Consumer({}) | Entity Id={} | Message={}",
					id, entityId, message);			
			bProcessed = false;
		} catch (Exception e) {
			LOGGER.error(
					"Consumer({}) | Entity Id={} | Exception while processing queue message | Exception Message={}",
					id, entityId, e.getLocalizedMessage());
			LOGGER.debug(
					"Consumer({}) | Entity Id={} | Message={}",
					id, entityId, message);				
			bProcessed = false;
		} finally {
			if (bProcessed) {
				LOGGER.info("Consumer({}) | Entity Id={} | Processed Message={}", id,entityId, message);
				channel.basicAck(envelope.getDeliveryTag(), false);
			} else {
				LOGGER.warn("Consumer({}) | Entity Id={} | Failed to process message | Failed Message={}", id,
						entityId, message);
				LOGGER.warn("Consumer({}) | Entity Id={} | Requeuing Message={}", id, entityId, message);
				channel.basicReject(envelope.getDeliveryTag(), true);
			}
		}

	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {

	}

	@Override
	public void handleRecoverOk(String consumerTag) {

	}

	@Override
	public void run() {
		try {
			LOGGER.info("Starting Consumer {}", id);
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured during consume | Consumer={} | Exception Message={}",
					id, e.getLocalizedMessage());
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}