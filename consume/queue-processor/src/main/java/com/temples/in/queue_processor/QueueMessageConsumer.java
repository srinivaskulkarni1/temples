package com.temples.in.queue_processor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.temples.in.consume_util.BeanConstants;

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
			AMQP.BasicProperties properties, byte[] body) throws IOException {

		String message = new String(body, "UTF-8");
		LOGGER.info("Consumer{}: Received message | {}", id, message);
		boolean bProcessed = false;

		try {
			MessageProcessor messageProcessor = (MessageProcessor) context
					.getBean(BeanConstants.MESSAGE_PROCESSOR);
			bProcessed = messageProcessor.process(id, message);
		} catch (BeansException e) {
			LOGGER.error(
					"Consumer{}: BeansException while processing queue message | {}",
					id, message);
			LOGGER.debug(
					"Consumer{}: Exception message | {}",
					id, e.getLocalizedMessage());		
			bProcessed = false;
		} catch (Exception e) {
			LOGGER.error(
					"Consumer{}: Exception while processing queue message | {}",
					id, message);
			LOGGER.debug(
					"Consumer{}: Exception message | {}",
					id, e.getLocalizedMessage());				
			bProcessed = false;
		} finally {
			if (bProcessed) {
				LOGGER.info("Consumer{}: processed message | {}", id, message);
				channel.basicAck(envelope.getDeliveryTag(), false);
			} else {
				LOGGER.warn("Consumer{}: failed to process message | {}", id,
						message);
				LOGGER.warn("Consumer{}: requeuing message | {}", id, message);
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
			LOGGER.info("Starting consumer {}", id);
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured during consume. Exception message is | {}",
					e.getLocalizedMessage());
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}