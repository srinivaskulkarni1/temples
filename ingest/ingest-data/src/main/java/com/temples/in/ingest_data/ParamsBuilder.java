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
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.ingest_util.BeanConstants;
import com.temples.in.ingest_util.NullPrimaryKeyException;

public class ParamsBuilder implements ApplicationContextAware, IParamsBulder {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public List<Params> buildTempleParams(Temple temple) {

		LOGGER.debug("Processing | Id={} | {}.buildTempleParams",
				temple.getId(), ParamsBuilder.class.getSimpleName());

		List<Params> paramsList = new ArrayList<Params>();
		
		if (temple.getId() != null) {
			Params params0 = (Params) context.getBean(BeanConstants.PARAMS);
			params0.setType(temple.getId().getClass());
			params0.setName(DBConstants.ID);
			params0.setValue(temple.getId());
			paramsList.add(params0);
		}else{
			throw new NullPrimaryKeyException("Id cannot be null");
		}
		
		String god = temple.getGod();
		if (god != null) {
			Params params1 = (Params) context.getBean(BeanConstants.PARAMS);
			params1.setType(god.getClass());
			params1.setName(DBConstants.TABLE_TEMPLE_GOD);
			params1.setValue(god);
			paramsList.add(params1);
		}

		String place = temple.getPlace();
		if (place != null) {
			Params params2 = (Params) context.getBean(BeanConstants.PARAMS);
			params2.setType(place.getClass());
			params2.setName(DBConstants.TABLE_TEMPLE_PLACE);
			params2.setValue(place);
			paramsList.add(params2);
		}

		String district = temple.getDistrict();
		if (district != null) {
			Params params3 = (Params) context.getBean(BeanConstants.PARAMS);
			params3.setType(district.getClass());
			params3.setName(DBConstants.TABLE_TEMPLE_DISTRICT);
			params3.setValue(district);
			paramsList.add(params3);
		}

		String state = temple.getState();
		if (state != null) {
			Params params4 = (Params) context.getBean(BeanConstants.PARAMS);
			params4.setType(state.getClass());
			params4.setName(DBConstants.TABLE_TEMPLE_STATE);
			params4.setValue(state);
			paramsList.add(params4);
		}

		LOGGER.debug("Processed | Id={} | {}.buildTempleParams",
				temple.getId(), ParamsBuilder.class.getSimpleName());

		return paramsList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
