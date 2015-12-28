package com.temples.in.ingest_resource.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.Temple;
import com.temples.in.ingest_data.IDataLoader;
import com.temples.in.ingest_util.BeanConstants;

public class TempleService implements ApplicationContextAware, ITempleService {

	private AbstractApplicationContext context;
	private IDataLoader dataLoader;
	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	
	@Override
	public Temple addTemple(Temple temple) {
		LOGGER.debug("Processing {}.addTemple", TempleService.class.getSimpleName());

		this.dataLoader = (IDataLoader) context.getBean(BeanConstants.DATA_LOADER);
		Temple newTemple = dataLoader.addTemple(temple);
		LOGGER.debug("Processed {}.addTemple", TempleService.class.getSimpleName());

		return newTemple;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;

	}
}
