package com.temples.in.query_data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.query_data.data_access.IDBConnection;
import com.temples.in.query_data.data_access.IEntityAll;
import com.temples.in.query_data.data_access.PreparedStatements;
import com.temples.in.query_data.exceptions.QueryDataException;
import com.temples.in.query_util.ErrorCodes;

@Component(value="templesall")
public class TemplesAll implements IEntityAll {

	@Autowired
	@Qualifier("cassandrastore")
	private IDBConnection dbConnection;

	@Autowired
	private PreparedStatements preparedStatements;

	private static Logger LOGGER = LoggerFactory.getLogger(TemplesAll.class);

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> List<T> getAll() {

		LOGGER.debug("Processing {}.getAll", TemplesAll.class.getSimpleName());
		List<T> results = new ArrayList<>();

		try {
			BoundStatement boundStatement = buildBoundStatement();
			ResultSet resultSet = dbConnection.getSession().execute(
					boundStatement);

			if (resultSet != null) {
				for (Row row : resultSet) {
					BaseEntity baseEntity = convertToEntity(row);
					results.add((T) baseEntity);
				}
			}

		} catch (Exception e) {
			LOGGER.error(
					"ErrorCode={} | Exception while processing get entity list | Exception Message={}",
					ErrorCodes.dbQueryError, e.getLocalizedMessage());
			throw new QueryDataException(ErrorCodes.dbQueryError,
					"Exception while processing get entity list | Exception Message="
							+ e.getLocalizedMessage());
		}
		LOGGER.debug("Processed {}.getAll", TemplesAll.class.getSimpleName());
		return results;

	}

	private BoundStatement buildBoundStatement() {
		BoundStatement boundStatement = new BoundStatement(
				preparedStatements.getAllTemples());
		return boundStatement;
	}

	@SuppressWarnings("unchecked")
	private <T extends BaseEntity> T convertToEntity(Row row) {
		ByteBuffer bytes = row.getBytes(DBConstants.ENTITY);
		String entityAsString = Conversions.bb_to_str(bytes);
		T baseEntity = (T) Conversions.getEntityFromJson(
				entityAsString, Temple.class);
		return baseEntity;
	}
}
