package com.temples.in.ingest_interface.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.ingest_data.data_loader.IDataLoader;

@Component(value = "templeservice")
public class TempleService implements ITempleService {

	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	@Autowired
	@Qualifier("templedataloader")
	private IDataLoader dataLoader;

	@Autowired
	@Qualifier("searchpublisher")
	private ISearchPublisher esIngester;

	@Autowired
	@Qualifier("queuepublisher")
	private IQueuePublisher queueManager;

	@Override
	public Temple addTemple(Temple temple) {
		LOGGER.debug("Processing | Entity Id={} | {}.addTemple",
				temple.getId(), TempleService.class.getSimpleName());

		// step 1
		Temple newTemple = (Temple) dataLoader.add(temple);

		// step 1.1
		if (newTemple == null) {
			LOGGER.warn(
					"Processed | Entity Id={} | Failed to publish entity to store",
					temple.getId());
			return null;
		}

		// step 2
		esIngester.postDataToSearcher(temple.getId(), temple);

		Map<String, Object> pkList = new HashMap<String, Object>();
		pkList.put(DBConstants.ID, newTemple.getId());

		// step 3
		boolean bSuccess = queueManager.enqueue(Action.POST, EntityType.TEMPLE,
				pkList);
		if (!bSuccess) {
			LOGGER.warn(
					"Processed | Entity Id={} | Failed to post entity information to queue",
					temple.getId());
			return null;
		}

		LOGGER.debug("Processed | Entity Id={} | {}.addTemple", temple.getId(),
				TempleService.class.getSimpleName());
		return newTemple;
	}
}
