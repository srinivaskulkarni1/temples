package com.temples.in.ingest_resource.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.ingest_data.DBConstants;
import com.temples.in.ingest_data.IDataLoader;
import com.temples.in.ingest_util.BeanConstants;

public class TempleService implements ApplicationContextAware, ITempleService {

	private AbstractApplicationContext context;
	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	
	@Override
	public Temple addTemple(Temple temple) {
		LOGGER.debug("Processing {}.addTemple", TempleService.class.getSimpleName());

		IDataLoader dataLoader = (IDataLoader) context.getBean(BeanConstants.DATA_LOADER);
		Temple newTemple = dataLoader.addTemple(temple);


		if (newTemple != null) {
			Map<String, String> pkList = new HashMap<String, String>();
			pkList.put(DBConstants.TABLE_TEMPLE_GOD, newTemple.getGod());
			pkList.put(DBConstants.TABLE_TEMPLE_PLACE, newTemple.getPlace());

			IQueueManager queueManager = (IQueueManager) context
					.getBean(BeanConstants.QUEUE_MANAGER);

			boolean bSuccess = queueManager.enqueue(Action.PUT,
					EntityType.TEMPLE, pkList);
			if (!bSuccess) {
				return null;
			}
		}
		
		LOGGER.debug("Processed {}.addTemple", TempleService.class.getSimpleName());
		return newTemple;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;

	}
}
