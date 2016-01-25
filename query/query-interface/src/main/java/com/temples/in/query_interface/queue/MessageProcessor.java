package com.temples.in.query_interface.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.cacher.CacheType;
import com.temples.in.cacher.IEHCacheManager;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.query_data.IDataLoader;

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
			throws Exception {

		LOGGER.debug("Entity Id={} | Processing {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		LOGGER.info("Entity Id={} | Processing entity", entityId);
		boolean bProcessed = false;

		if (entityInfo != null) {
			if (Action.POST.equals(entityInfo.getAction())) {
				bProcessed = handlePOSTRequest(entityId, entityInfo);
			} else if (Action.PUT.equals(entityInfo.getAction())) {
				throw new Exception("PUT request not implemented!!!");
			} else if (Action.DELETE.equals(entityInfo.getAction())) {
				throw new Exception("DELETE request not implemented!!!");
			} else{
				LOGGER.warn("Unsupported Action. Unable to process | Entity Id={}", entityId);
				throw new Exception("Unable to process. Information provided cannot be processed");
			}
		}else{
			LOGGER.warn("Entity Info is empty. Unable to process | Entity Id={}", entityId);
			throw new Exception("Unable to process. Information provided cannot be processed");
		}

		LOGGER.debug("Entity Id={} | Processed {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		return bProcessed;

	}

	private boolean handlePOSTRequest(String entityId, EntityInfo entityInfo)
			throws Exception {
		LOGGER.debug("Entity Id={} | Processing {}.handlePUTRequest", entityId,
				MessageProcessor.class.getSimpleName());

		if (EntityType.TEMPLE.equals(entityInfo.getEntityType())) {
			Temple temple = (Temple) dataLoader.getOne(entityInfo
					.getPrimaryKey());
			if (temple != null) {
				LOGGER.debug("Entity Id={} | Adding entity to cache", entityId);
				ehCacheManager.put(entityId, temple, CacheType.Temples);;
			} else {
				LOGGER.warn(
						"Entity not found in data store. Ignoring queue message. | Entity Id={}",
						entityId);
			}
		}

		LOGGER.debug("Entity Id={} | Processed {}.handlePUTRequest", entityId,
				MessageProcessor.class.getSimpleName());
		return true;
	}
}