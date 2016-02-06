package com.temples.in.queue_listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.cacher.CacheType;
import com.temples.in.cacher.IEHCacheManager;
import com.temples.in.common_utils.ErrorCodes;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.query_data.IDataLoader;
import com.temples.in.queue_listener.exceptions.QueueProcessingException;

@Component(value="messageprocessor")
public class MessageProcessor implements IMessageProcessor {

	@Autowired
	@Qualifier("templedataloader")
	private IDataLoader dataLoader;

	@Autowired
	@Qualifier("ehcachemanager")
	private IEHCacheManager ehCacheManager;

	private static Logger LOGGER = LoggerFactory
			.getLogger(MessageProcessor.class);

	public MessageProcessor() {
	}


	@Override
	public boolean process(String entityId, EntityInfo entityInfo)
			throws QueueProcessingException {

		LOGGER.debug("Entity Id={} | Processing {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		LOGGER.info("Entity Id={} | Processing message", entityId);
		boolean bProcessed = false;

		if (entityInfo != null) {
			if (Action.POST.equals(entityInfo.getAction())) {
				bProcessed = handlePOSTRequest(entityId, entityInfo);
			} else if (Action.PUT.equals(entityInfo.getAction())) {
				throw new QueueProcessingException(ErrorCodes.queueError, "Entity Id=" + entityId + ": PUT request not implemented!!!");
			} else if (Action.DELETE.equals(entityInfo.getAction())) {
				throw new QueueProcessingException(ErrorCodes.queueError, "Entity Id=" + entityId + ": DELETE request not implemented!!!");
			} else{
				LOGGER.warn("Entity Id={} | Request not supported. Request must be one of the following: POST, PUT, DELETE", entityId);
				throw new QueueProcessingException(ErrorCodes.queueError, "Entity Id=" + entityId + ": Request not supported. Request must be one of the following: POST, PUT, DELETE");
			}
		}else{
			LOGGER.warn("Entity Id={} | Message retrieved from the queue is empty. Unable to process the request", entityId);
			throw new QueueProcessingException(ErrorCodes.queueError, "Entity Id=" + entityId + ": Message retrieved from the queue is empty. Unable to process the request");
		}

		LOGGER.debug("Entity Id={} | Processed {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		return bProcessed;

	}

	private boolean handlePOSTRequest(String entityId, EntityInfo entityInfo) {
		LOGGER.debug("Entity Id={} | Processing {}.handlePOSTRequest", entityId,
				MessageProcessor.class.getSimpleName());

		if (EntityType.TEMPLE.equals(entityInfo.getEntityType())) {
			Temple temple = (Temple) dataLoader.getOne(entityInfo
					.getPrimaryKey());
			if (temple != null) {
				ehCacheManager.put(entityId, temple, CacheType.Temples);;
			} else {
				LOGGER.warn(
						"Entity Id={} | Ignoring queue message",
						entityId);
			}
		}

		LOGGER.debug("Entity Id={} | Processed {}.handlePOSTRequest", entityId,
				MessageProcessor.class.getSimpleName());
		return true;
	}
}