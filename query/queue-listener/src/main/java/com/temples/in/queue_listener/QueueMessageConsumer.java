package com.temples.in.queue_listener;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.queue_listener.exceptions.QueueProcessingException;

@Component 
class QueueMessageConsumer implements Consumer {

	private Channel channel;
	private String queueName;
	
	@Autowired
	@Qualifier("messageprocessor")
	private IMessageProcessor messageProcessor;

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

		LOGGER.debug("Received queue message={}", message);

		entityInfo = (EntityInfo) Conversions.getEntityFromJson(message,
				EntityInfo.class);

		
		String entityId = (String) entityInfo.getPrimaryKey().getPrimaryKeys()
				.get(DBConstants.ID);
		
		LOGGER.info("Entity Id={} | Received queue message", entityId);

		try {
			bProcessed = messageProcessor.process(entityId, entityInfo);
		} catch (QueueProcessingException e) {
			LOGGER.debug("Entity Id={} | Message={}", entityId, message);
			bProcessed = false;
		} finally {
			if (bProcessed) {
				LOGGER.debug("Entity Id={} | Processed Message={}", entityId,
						message);
				LOGGER.info("Entity Id={} | Successfully processed queue message", entityId);
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

	void init() {
		try {
			LOGGER.info("Starting Queue Consumer...");
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured while initializing queue | Exception Message={}",
					e.getLocalizedMessage());
		}

	}
}