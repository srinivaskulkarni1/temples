package com.temples.in.ingest_data;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.Temple;
import com.temples.in.ingest_util.BeanConstants;

public class ParamsBuilder implements ApplicationContextAware, IParamsBulder {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public List<Params> buildTempleParams(Temple temple) {

		LOGGER.debug("Processing {}.buildTempleParams",
				ParamsBuilder.class.getSimpleName());

		List<Params> paramsList = new ArrayList<Params>();

		if (temple.getGod() != null) {
			Params params1 = (Params) context.getBean(BeanConstants.PARAMS);
			params1.setType(temple.getGod().getClass());
			params1.setName(DBConstants.TABLE_TEMPLE_GOD);
			params1.setValue(temple.getGod());
			paramsList.add(params1);
		}

		if (temple.getPlace() != null) {
			Params params2 = (Params) context.getBean(BeanConstants.PARAMS);
			params2.setType(temple.getPlace().getClass());
			params2.setName(DBConstants.TABLE_TEMPLE_PLACE);
			params2.setValue(temple.getPlace());
			paramsList.add(params2);
		}

		if (temple.getDistrict() != null) {
			Params params3 = (Params) context.getBean(BeanConstants.PARAMS);
			params3.setType(temple.getDistrict().getClass());
			params3.setName(DBConstants.TABLE_TEMPLE_DISTRICT);
			params3.setValue(temple.getDistrict());
			paramsList.add(params3);
		}

		if (temple.getState() != null) {
			Params params4 = (Params) context.getBean(BeanConstants.PARAMS);
			params4.setType(temple.getState().getClass());
			params4.setName(DBConstants.TABLE_TEMPLE_STATE);
			params4.setValue(temple.getState());
			paramsList.add(params4);
		}

		LOGGER.debug("Processed {}.buildTempleParams",
				ParamsBuilder.class.getSimpleName());

		return paramsList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
