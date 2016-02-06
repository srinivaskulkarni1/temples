package com.temples.in.query_data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.PrimaryKey;
import com.temples.in.query_data.data_access.IEntityAll;
import com.temples.in.query_data.data_access.IEntityById;

@Component(value = "templedataloader")
public class TempleDataLoader implements IDataLoader {

	@Autowired
	@Qualifier("templesbyid")
	IEntityById entityById;

	@Autowired
	@Qualifier("templesall")
	IEntityAll entityAll;

	@Autowired
	@Qualifier("paramsbuilder")
	private IParamsBulder paramsBuilder;

	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleDataLoader.class);

	@Override
	public List<BaseEntity> getAll() {
		LOGGER.debug("Processing {}.getAll",
				TempleDataLoader.class.getSimpleName());

		List<BaseEntity> all = entityAll.getAll();

		LOGGER.debug("Processed {}.getAll",
				TempleDataLoader.class.getSimpleName());

		return all;
	}

	@Override
	public BaseEntity getOne(PrimaryKey primaryKey) {
		LOGGER.debug("Processing {}.getOne",
				TempleDataLoader.class.getSimpleName());

		String entityId = (String) primaryKey.getPrimaryKeys().get(
				DBConstants.ID);
		
		LOGGER.info("Entity Id={} | Retrieving entity from data store", entityId);

		List<Params> params = paramsBuilder.build(entityId, primaryKey);
		List<BaseEntity> list = entityById.getById(params);


		if (list.size() <= 0) {
			LOGGER.warn("Entity Id={} | Entity not found in data store", entityId);
			LOGGER.debug("Processed {}.getOne",
					TempleDataLoader.class.getSimpleName());
			return null;
		}

		LOGGER.warn("Entity Id={} | Successfully retrieved entity from data store", entityId);
		LOGGER.debug("Processed {}.getOne",
				TempleDataLoader.class.getSimpleName());
		return list.get(0);
	}

}
