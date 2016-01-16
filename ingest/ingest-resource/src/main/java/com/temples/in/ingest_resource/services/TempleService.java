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
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.ingest_data.data_loader.IDataLoader;
import com.temples.in.ingest_util.BeanConstants;

public class TempleService implements ApplicationContextAware, ITempleService {

	private AbstractApplicationContext context;
	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	
	@Override
	public Temple addTemple(Temple temple) {
		LOGGER.debug("Processing | Entity Id={} | {}.addTemple", temple.getId(), TempleService.class.getSimpleName());

		//step 1
		IDataLoader dataLoader = (IDataLoader) context.getBean(BeanConstants.TEMPLE_DATA_LOADER);
		Temple newTemple = (Temple) dataLoader.add(temple);

		if(newTemple != null){

			//step 2
			IESIngester esIngester = (IESIngester) context.getBean(BeanConstants.ES_INGESTER);
			esIngester.postDataToSearcher(temple.getId(), temple);

			//step 3
			Map<String, Object> pkList = new HashMap<String, Object>();
			pkList.put(DBConstants.ID, newTemple.getId());
			
			IQueueManager queueManager = (IQueueManager) context
					.getBean(BeanConstants.QUEUE_MANAGER);
			
			boolean bSuccess = queueManager.enqueue(Action.POST,
					EntityType.TEMPLE, pkList);
			if (!bSuccess) {
				return null;
			}
		}
		
		LOGGER.debug("Processed | Entity Id={} | {}.addTemple", temple.getId(), TempleService.class.getSimpleName());
		return newTemple;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;

	}
}
