package com.temples.in.query_interface.queue;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.query_data.IDataLoader;
import com.temples.in.query_interface.cache.EHCacheManager;

public class MessageProcessor {

	@Autowired
	private IDataLoader dataLoader;

	@Autowired
	private EHCacheManager ehCacheManager;

	private static Logger LOGGER = LoggerFactory
			.getLogger(MessageProcessor.class);

	public MessageProcessor() {
	}

	public boolean process(String entityId, EntityInfo entityInfo)
			throws Exception {

		LOGGER.debug("Entity Id={} | Processing {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		LOGGER.info("Entity Id={} | Processing entity", entityId);
		boolean bProcessed = false;

		if (entityInfo != null) {
			if (entityInfo.getAction().equals(Action.POST)) {
				bProcessed = handlePOSTRequest(entityId, entityInfo);
			} else if (entityInfo.getAction().equals(Action.PUT)) {
				throw new Exception("PUT request not implemented!!!");
			} else if (entityInfo.getAction().equals(Action.DELETE)) {
				throw new Exception("DELETE request not implemented!!!");
			}
		}

		LOGGER.debug("Entity Id={} | Processed {}.process", entityId,
				MessageProcessor.class.getSimpleName());

		return bProcessed;

	}

	private boolean handlePOSTRequest(String entityId, EntityInfo entityInfo)
			throws Exception {
		LOGGER.debug("Entity Id={} | Processing {}.handlePUTRequest", entityId,
				MessageProcessor.class.getSimpleName());

		if (entityInfo.getEntityType().equals(EntityType.TEMPLE)) {
			Temple temple = (Temple) dataLoader.getOne(entityInfo
					.getPrimaryKey());
			if (temple != null) {
				LOGGER.debug("Entity Id={} | Adding entity to cache", entityId);
				ehCacheManager.getTemplesCahce().put(
						new Element(entityId, temple));
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