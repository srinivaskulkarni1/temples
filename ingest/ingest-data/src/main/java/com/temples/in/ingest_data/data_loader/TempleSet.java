package com.temples.in.ingest_data.data_loader;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.temples.in.common_utils.Conversions;
import com.temples.in.common_utils.ErrorCodes;
import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.exceptions.QueryDataException;
import com.temples.in.ingest_data.data_access.IDBConnection;
import com.temples.in.ingest_data.data_access.ISetEntity;
import com.temples.in.ingest_data.data_access.PreparedStatements;

@Component(value="templeset")
public class TempleSet implements ISetEntity {

	@Autowired
	PreparedStatements preparedStatements;

	@Autowired
	@Qualifier("cassandrastore")
	IDBConnection connection;

	private static Logger LOGGER = LoggerFactory.getLogger(TempleSet.class);

	@Override
	public void setEntity(BaseEntity baseEntity) {

		LOGGER.debug("Processing | {}.setEntity | Entity Id={}",
				this.getClass().getSimpleName(),
				baseEntity.getId());

		BoundStatement boundStatement = new BoundStatement(
				preparedStatements.getTempleSet());

		boundStatement = boundStatement.setString(DBConstants.ID,
				baseEntity.getId());

		String entityAsString = Conversions.getJsonFromEntity(baseEntity);

		boundStatement = boundStatement.setBytes(DBConstants.ENTITY,
				ByteBuffer.wrap(entityAsString.getBytes()));

		try {
			LOGGER.info("Entity Id={} | Saving entity in data store",
					baseEntity.getId());
			connection.getSession().execute(boundStatement);
			LOGGER.info("Entity Id={} | Successfully stored entity in data store",
					baseEntity.getId());
		} catch (Exception e) {
			LOGGER.error(
					"ErrorCode={} | Exception while processing publishing data to store | Exception Message={}",
					ErrorCodes.dbQueryError, e.getLocalizedMessage());
			throw new QueryDataException(ErrorCodes.dbQueryError,
					"Exception while processing publishing data to store | Exception Message="
							+ e.getLocalizedMessage());
		}

		LOGGER.debug("Processed | {}.setEntity | Entity Id={}",
				this.getClass().getSimpleName(),
				baseEntity.getId());

	}

}
