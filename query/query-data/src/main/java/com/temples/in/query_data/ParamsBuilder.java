package com.temples.in.query_data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.wrapper.PrimaryKey;
import com.temples.in.query_util.BeanConstants;

public class ParamsBuilder implements ApplicationContextAware, IParamsBulder {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public List<Params> build(String entityId, PrimaryKey primaryKey) {

		LOGGER.debug("Entity Id={} | Processing {}.build", entityId,
				ParamsBuilder.class.getSimpleName());

		Map<String, Object> pkList = primaryKey.getPrimaryKeys();
		List<Params> paramsList = new ArrayList<Params>();
		
		for (Map.Entry<String, Object> entry : pkList.entrySet())
		{
			Params params = (Params) context.getBean(BeanConstants.PARAMS);
			params.setType(entry.getValue().getClass());
			params.setName(entry.getKey());
			params.setValue(entry.getValue());
			paramsList.add(params);

		}
		
		LOGGER.debug("Entity Id={} | Processed {}.build", entityId,
				ParamsBuilder.class.getSimpleName());

		return paramsList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
