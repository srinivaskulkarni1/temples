package com.temples.in.queue_processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.temples.in.consume_data.ITempleDataLoader;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.Entity;
import com.temples.in.data_model.wrapper.EntityInfo;

public class MessageProcessor implements ApplicationContextAware {

	private AbstractApplicationContext context;
	private ITempleDataLoader dataLoader;
	private static Logger LOGGER = LoggerFactory
			.getLogger(MessageProcessor.class);

	public MessageProcessor() {
	}

	public boolean process(int consumerId, String message) throws Exception {
		LOGGER.info("Consumer{}: processing message | {}", consumerId, message);
		Gson gson = new Gson();
		EntityInfo entityInfo = null;
		boolean bProcessed = false;
		
		try {
			entityInfo = gson.fromJson(message, EntityInfo.class);
		} catch (JsonSyntaxException e) {
			LOGGER.error("Consumer{}: Invalid message format | {}", consumerId,
					message);
			throw e;
		}

		if (entityInfo != null) {
			if (entityInfo.getAction().equals(Action.PUT)) {
				bProcessed = handlePUTRequest(consumerId, entityInfo);
			} else if (entityInfo.getAction().equals(Action.POST)) {
				throw new Exception("POST request not implemented!!!");
			} else if (entityInfo.getAction().equals(Action.DELETE)) {
				throw new Exception("DELETE request not implemented!!!");
			}
		}

		return bProcessed;

	}

	private boolean handlePUTRequest(int consumerId, EntityInfo entityInfo) throws Exception {
		LOGGER.debug("Consumer{}: Processing {}.handlePUTRequest", consumerId, MessageProcessor.class.getSimpleName());

		if (entityInfo.getEntity().equals(Entity.TEMPLE)) {
			try {
				dataLoader = (ITempleDataLoader) context
						.getBean(BeanConstants.TEMPLE_DATA_LOADER);
			} catch (BeansException e) {
				LOGGER.error(
						"Consumer{}: BeansException while processing queue message",
						consumerId);
				LOGGER.debug("Consumer{}: Exception message | {}", consumerId,
						e.getLocalizedMessage());
				return false;
			}

			if (dataLoader != null) {
				if (entityInfo.getEntity().equals(Entity.TEMPLE)) {
					 Temple temple = dataLoader.getTemple(entityInfo.getPrimaryKey());
					 if(temple != null){
					 LOGGER.debug("Consumer{}: Request Entity | Temple | Primary Key | {}", consumerId, temple.getGod() + "," + temple.getPlace());
					 }
				}
			}
		}
		return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}

}