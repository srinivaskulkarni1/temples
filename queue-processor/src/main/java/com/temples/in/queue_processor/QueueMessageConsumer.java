package com.temples.in.queue_processor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class QueueMessageConsumer implements Consumer, Runnable {

	private Channel channel;
	private String queueName;
	private int id;
	private static Logger LOGGER = LoggerFactory
			.getLogger(QueueMessageConsumer.class);

	public QueueMessageConsumer(Channel channel, String queueName, int id) {
		this.channel = channel;
		this.queueName = queueName;
		this.id = id;
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
		try {
			LOGGER.info("Consumer{} Recieved message | {}", id, message);
		} catch (Exception e) {
			LOGGER.error("Exception while processing queue message | {}",
					message);
		} finally {
			channel.basicAck(envelope.getDeliveryTag(), false);
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
			LOGGER.debug("Starting consumer {}", id);
			channel.basicConsume(queueName, false, this);
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured during consume. Exception message is | {}",
					e.getLocalizedMessage());
		}

	}
}