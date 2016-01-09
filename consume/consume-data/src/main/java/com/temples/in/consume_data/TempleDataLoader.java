package com.temples.in.consume_data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.temples.in.consume_data.data_access.IDBConnection;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.data_model.wrapper.PrimaryKey;

public class TempleDataLoader implements ApplicationContextAware,
		ITempleDataLoader {

	private AbstractApplicationContext context;
	private IDBConnection dbConnection;
	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleDataLoader.class);

/*	@Override
	public List<Temple> getTempleList() {
		LOGGER.debug("Processing {}.getTempleList",
				TempleDataLoader.class.getSimpleName());

		dbConnection = getDBConnection();

		List<Temple> templeList = new ArrayList<Temple>();
		ResultSet resultSet = dbConnection.getAll(DBConstants.TABLE_TEMPLE);

		if (resultSet != null) {
			Mapper<Temple> mapper = dbConnection.getManager().mapper(
					Temple.class);
			Result<Temple> results = mapper.map(resultSet);

			if (results != null) {
				for (Temple temple : results) {
					templeList.add(temple);
				}
			}
		}
		LOGGER.debug("Processed {}.getTempleList",
				TempleDataLoader.class.getSimpleName());

		return templeList;
	}*/

	private IDBConnection getDBConnection() {
		return (IDBConnection) context.getBean(BeanConstants.DB_CONNECTION);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}

	@Override
	public synchronized Temple getTemple(int consumerId, PrimaryKey primaryKey) {
		
		String entityId = (String) primaryKey.getPrimaryKeys().get(DBConstants.ID);

		LOGGER.debug("Consumer({}) | Entity Id={} | Processing {}.getTemple", consumerId, entityId,
				TempleDataLoader.class.getSimpleName());

		dbConnection = getDBConnection();
		IParamsBulder paramsBuilder = getParamsBuilder();
		Temple temple = null;

		ResultSet resultSet = dbConnection.getOne(QueryStrings.TEMPLE_SELECT_ONE,
				QueryStrings.TEMPLE_SELECT_QUERY,
				paramsBuilder.build(consumerId, entityId, primaryKey));

		if (resultSet != null) {
			for (Row row : resultSet) {
				temple = (Temple) context.getBean(BeanConstants.TEMPLE);
				temple.setId(row.getString(DBConstants.ID));
				temple.setGod(row.getString(DBConstants.TABLE_TEMPLE_GOD));
				temple.setPlace(row.getString(DBConstants.TABLE_TEMPLE_PLACE));
				temple.setDistrict(row.getString(DBConstants.TABLE_TEMPLE_DISTRICT));
				temple.setState(row.getString(DBConstants.TABLE_TEMPLE_STATE));
			}
		}
		
		LOGGER.debug("Consumer({}) | Entity Id={} | Processed {}.getTemple", consumerId, entityId,
				TempleDataLoader.class.getSimpleName());
		return temple;
	}

	private IParamsBulder getParamsBuilder() {
		return (IParamsBulder) context.getBean(BeanConstants.PARAMS_BUILDER);
	}
}
