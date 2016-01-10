package com.temples.in.query_resource.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.Temple;
import com.temples.in.query_processor.processor.ESQueryProcessor;
import com.temples.in.query_util.BeanConstants;


public class TempleService implements ApplicationContextAware, ITempleService {

	private AbstractApplicationContext context;
	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);
	private ESQueryProcessor esQueryProcessor;
	
	@Override
	public List<Temple> getTemples() {
		LOGGER.debug("Processing {}.getTemples", TempleService.class.getSimpleName());

		esQueryProcessor = (ESQueryProcessor) context.getBean(BeanConstants.ES_QUERY_PROCESSOR);
		List<Temple> temples = esQueryProcessor.getAll(Resources.TEMPLES, Temple.class);
		LOGGER.debug("Processed {}.getTemples", TempleService.class.getSimpleName());

		return temples;
	}

	@Override
	public Temple getTemple(String id) {
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;

	}

}
