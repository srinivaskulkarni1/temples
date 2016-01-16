package com.temples.in.query_interface.queue;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.query_util.BeanConstants;

public class QueueMessageConsumer implements Consumer, ApplicationContextAware {

	private Channel channel;
	private String queueName;
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
			AMQP.BasicProperties properties, byte[] body) throws IOException,
			JsonSyntaxException {

		String message = new String(body, "UTF-8");
		boolean bProcessed = false;
		EntityInfo entityInfo = null;

		LOGGER.info("Recieved queue message={}", message);

		entityInfo = (EntityInfo) Conversions.getEntityFromJson(message,
				EntityInfo.class);

		String entityId = (String) entityInfo.getPrimaryKey().getPrimaryKeys()
				.get(DBConstants.ID);
		try {
			MessageProcessor messageProcessor = (MessageProcessor) context
					.getBean(BeanConstants.MESSAGE_PROCESSOR);
			bProcessed = messageProcessor.process(entityId, entityInfo);
		} catch (BeansException e) {
			LOGGER.error(
					"Entity Id={} | BeansException while processing queue message | Exception Message={}",
					entityId, e.getLocalizedMessage());
			LOGGER.debug("Entity Id={} | Message={}", entityId, message);
			bProcessed = false;
		} catch (Exception e) {
			LOGGER.error(
					"Entity Id={} | Exception while processing queue message | Exception Message={}",
					entityId, e.getLocalizedMessage());
			LOGGER.debug("Entity Id={} | Message={}", entityId, message);
			bProcessed = false;
		} finally {
			if (bProcessed) {
				LOGGER.info("Entity Id={} | Processed Message={}", entityId,
						message);
				channel.basicAck(envelope.getDeliveryTag(), false);
			} else {
				LOGGER.warn(
						"Entity Id={} | Failed to process message | Failed Message={}",
						entityId, message);
				LOGGER.warn("Entity Id={} | Requeuing Message={}", entityId,
						message);
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

	public void init() {
		LOGGER.debug("Initializing {}...",
				QueueMessageConsumer.class.getSimpleName());

		try {
			LOGGER.info("Starting Queue Consumer...");
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured while initializing queue | Exception Message={}",
					e.getLocalizedMessage());
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}