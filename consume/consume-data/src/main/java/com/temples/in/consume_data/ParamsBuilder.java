package com.temples.in.consume_data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.wrapper.PrimaryKey;

public class ParamsBuilder implements ApplicationContextAware, IParamsBulder {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public List<Params> build(PrimaryKey primaryKey) {

		LOGGER.debug("Processing {}.build",
				ParamsBuilder.class.getSimpleName());

		Map<String, String> pkList = primaryKey.getPrimaryKeys();
		List<Params> paramsList = new ArrayList<Params>();
		
		for (Map.Entry<String, String> entry : pkList.entrySet())
		{
			Params params = (Params) context.getBean(BeanConstants.PARAMS);
			params.setType(entry.getValue().getClass());
			params.setName(entry.getKey());
			params.setValue(entry.getValue());
			paramsList.add(params);

		}
		
		LOGGER.debug("Processed {}.build",
				ParamsBuilder.class.getSimpleName());

		return paramsList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
