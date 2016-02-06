package com.temples.in.ingest_data.data_loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.EntityGroup;
import com.temples.in.ingest_data.data_access.ISetEntity;

@Component(value = "templedataloader")
public class TempleDataLoader implements IDataLoader {

	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleDataLoader.class);

	@Autowired
	@Qualifier("templeset")
	private ISetEntity templeSet;

	@Override
	public BaseEntity add(BaseEntity entity) {
		LOGGER.debug("Processing | Entity Group={}, Entity Id={} | {}.add",
				EntityGroup.TEMPLES, entity.getId(), this.getClass()
						.getSimpleName());

		templeSet.setEntity(entity);

		LOGGER.debug("Processed | Entity Group={}, Entity Id={} | {}.add",
				EntityGroup.TEMPLES, entity.getId(), this.getClass()
						.getSimpleName());

		return entity;
	}
}
