package com.temples.in.queue_processor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.google.gson.Gson;
import com.temples.in.consume_data.ITempleDataLoader;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;

public class MessageProcessor implements ApplicationContextAware {

	private AbstractApplicationContext context;
	private ITempleDataLoader dataLoader;
	private static Logger LOGGER = LoggerFactory
			.getLogger(MessageProcessor.class);

	public MessageProcessor() {
	}

	public boolean process(int consumerId, String entityId, EntityInfo entityInfo) throws Exception {

		LOGGER.debug("Consumer({}) | Entity Id={} | Processing {}.process", consumerId, entityId,
				MessageProcessor.class.getSimpleName());

		LOGGER.info("Consumer({}) | Entity Id={} | Processing entity", consumerId, entityId);
		boolean bProcessed = false;

		if (entityInfo != null) {
			if (entityInfo.getAction().equals(Action.PUT)) {
				bProcessed = handlePUTRequest(consumerId, entityId, entityInfo);
			} else if (entityInfo.getAction().equals(Action.POST)) {
				throw new Exception("POST request not implemented!!!");
			} else if (entityInfo.getAction().equals(Action.DELETE)) {
				throw new Exception("DELETE request not implemented!!!");
			}
		}

		LOGGER.debug("Consumer({}) | Entity Id={} | Processed {}.process", consumerId, entityId,
				MessageProcessor.class.getSimpleName());

		return bProcessed;

	}

	private boolean handlePUTRequest(int consumerId, String entityId, EntityInfo entityInfo)
			throws Exception {
		LOGGER.debug("Consumer({}) | Entity Id={} | Processing {}.handlePUTRequest", consumerId, entityId,
				MessageProcessor.class.getSimpleName());

		if (entityInfo.getEntityType().equals(EntityType.TEMPLE)) {
			try {
				dataLoader = (ITempleDataLoader) context
						.getBean(BeanConstants.TEMPLE_DATA_LOADER);
			} catch (BeansException e) {
				LOGGER.error(
						"Consumer({}) | Entity Id={} | BeansException while processing queue message | Exception Message={}",
						consumerId, entityId, e.getLocalizedMessage());
				return false;
			}

			if (dataLoader != null) {
				if (entityInfo.getEntityType().equals(EntityType.TEMPLE)) {
					Temple temple = dataLoader.getTemple(consumerId, entityInfo
							.getPrimaryKey());
					if (temple != null) {
						postDataToSearcher(consumerId, entityId, temple);
					}
				}
			}
		}
		LOGGER.debug("Consumer({}) | Entity Id={} | Processed {}.handlePUTRequest", consumerId, entityId,
				MessageProcessor.class.getSimpleName());
		return true;
	}

	private void postDataToSearcher(int consumerId, String entityId, Temple temple) throws RuntimeException {

		LOGGER.debug("Consumer({}) | Entity Id={} | Processing {}.postDataToSearcher", consumerId, entityId,
				MessageProcessor.class.getSimpleName());

		Gson gson = new Gson();
		String templeJson = gson.toJson(temple);
		// to be moved to config
		String requestURL = "http://localhost:9200/temples/temple";

		LOGGER.info("Consumer({}) | Entity Id={} | Posting data to searcher", consumerId,
				entityId);
		
		LOGGER.debug("Consumer({}) | Entity Id={} | Posting data to searcher | URL={} | Message={}", consumerId,
				entityId, requestURL, templeJson);

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(requestURL);
		Invocation.Builder invocationBuilder =  webTarget.request();
		Response response = invocationBuilder.post(Entity.json(templeJson));
				
		if (response.getStatus() != 201) {
			throw new RuntimeException("Consumer(" + consumerId + ") | Entity Id=" + entityId + "Failed to post data to searcher | HTTP ERROR CODE : "
					+ response.getStatus());
		}

		LOGGER.info("Consumer({}) | Entity Id={} | Successfully posted data to searcher", consumerId,
				entityId);
		LOGGER.debug("Consumer({}) | Entity Id={} | Processed {}.postDataToSearcher", consumerId, entityId,
				MessageProcessor.class.getSimpleName());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}

}