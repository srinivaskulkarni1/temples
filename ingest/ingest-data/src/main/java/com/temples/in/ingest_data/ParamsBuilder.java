package com.temples.in.ingest_data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.ingest_util.BeanConstants;
import com.temples.in.ingest_util.NullPrimaryKeyException;

public class ParamsBuilder implements ApplicationContextAware, IParamsBulder {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public List<Params> buildParams(BaseEntity entity) {

		LOGGER.debug("Processing | Entity Id={} | {}.buildParams",
				entity.getId(), ParamsBuilder.class.getSimpleName());

		List<Params> paramsList = new ArrayList<Params>();
		
		if (entity.getId() != null) {
			Params params0 = (Params) context.getBean(BeanConstants.PARAMS);
			params0.setType(entity.getId().getClass());
			params0.setName(DBConstants.ID);
			params0.setValue(entity.getId());
			paramsList.add(params0);
		}else{
			throw new NullPrimaryKeyException("Id cannot be null");
		}
		
		Params params1 = (Params) context.getBean(BeanConstants.PARAMS);
		params1.setType(entity.getClass());
		params1.setName(DBConstants.ENTITY);
		params1.setValue(entity);
		paramsList.add(params1);

		LOGGER.debug("Processed | Entity Id={} | {}.buildParams",
				entity.getId(), ParamsBuilder.class.getSimpleName());

		return paramsList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
