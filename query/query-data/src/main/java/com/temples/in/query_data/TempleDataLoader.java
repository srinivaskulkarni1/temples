package com.temples.in.query_data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.PrimaryKey;
import com.temples.in.query_data.data_access.IDBConnection;

@Component(value="templedataloader")
public class TempleDataLoader implements IDataLoader {

	@Autowired
	@Qualifier("dbconnection")
	private IDBConnection dbConnection;

	@Autowired
	@Qualifier("paramsbuilder")
	private IParamsBulder paramsBuilder;

	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleDataLoader.class);

	@Override
	public List<BaseEntity> getAll() {
		LOGGER.debug("Processing {}.getAll",
				TempleDataLoader.class.getSimpleName());

		List<BaseEntity> templeList = new ArrayList<BaseEntity>();
		ResultSet resultSet = dbConnection
				.getAll(QueryStrings.TEMPLE_SELECT_ALL);

		if (resultSet != null) {
			for (Row row : resultSet) {
				Temple temple = convertToEntity(row);
				templeList.add(temple);
			}
		}
		LOGGER.debug("Processed {}.getAll",
				TempleDataLoader.class.getSimpleName());

		return templeList;
	}

	@Override
	public synchronized BaseEntity getOne(PrimaryKey primaryKey) {

		String entityId = (String) primaryKey.getPrimaryKeys().get(
				DBConstants.ID);

		LOGGER.debug("Entity Id={} | Processing {}.getOne", entityId,
				TempleDataLoader.class.getSimpleName());

		Temple temple = null;

		ResultSet resultSet = dbConnection.getOne(
				QueryStrings.TEMPLE_SELECT_ONE,
				paramsBuilder.build(entityId, primaryKey));

		if (resultSet != null) {
			temple = convertToEntity(resultSet.one());
		}

		LOGGER.debug("Entity Id={} | Processed {}.getOne", entityId,
				TempleDataLoader.class.getSimpleName());
		return temple;
	}

	private Temple convertToEntity(Row row) {
		ByteBuffer bytes = row.getBytes(DBConstants.ENTITY);
		String entityAsString = Conversions.bb_to_str(bytes);
		Temple temple = (Temple) Conversions.getEntityFromJson(entityAsString,
				Temple.class);
		return temple;
	}

}
