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
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.query_data.data_access.IDBConnection;
import com.temples.in.query_data.data_access.IEntityById;
import com.temples.in.query_data.data_access.PreparedStatements;
import com.temples.in.query_data.exceptions.QueryDataException;
import com.temples.in.query_util.ErrorCodes;

@Component(value="templesbyid")
public class TemplesById implements IEntityById {

	@Autowired
	@Qualifier("cassandrastore")
	private IDBConnection dbConnection;

	@Autowired
	private PreparedStatements preparedStatements;

	private static Logger LOGGER = LoggerFactory.getLogger(TemplesById.class);


	@Override
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> List<T> getById(List<Params> params) {

		LOGGER.debug("Processing {}.getById", TemplesById.class.getSimpleName());

		List<ResultSetFuture> futures = new ArrayList<>();
		List<T> results = new ArrayList<>();

		try {

			for (Params param : params) {
				BoundStatement boundStatement = buildBoundStatement(param);
				ResultSetFuture resultSetFuture = dbConnection.getSession()
						.executeAsync(boundStatement);
				futures.add(resultSetFuture);

			}

			for (ResultSetFuture future : futures) {
				ResultSet rs = future.getUninterruptibly();
				if (rs != null) {
					Row row = rs.one();
					if (row != null) {
						results.add((T) convertToEntity(row));
					}
				}
			}
		} catch (Exception e) {
			for (ResultSetFuture future : futures) {
				future.cancel(true);
			}
			LOGGER.error(
					"ErrorCode={} | Exception while processing get entity by Id | Exception Message={}",
					ErrorCodes.dbQueryError, e.getLocalizedMessage());
			throw new QueryDataException(ErrorCodes.dbQueryError,
					"Exception while processing get entity by Id | Exception Message="
							+ e.getLocalizedMessage());
		}

		LOGGER.debug("Processed {}.getById", TemplesById.class.getSimpleName());

		return results;

	}

	private BoundStatement buildBoundStatement(Params param) {
		BoundStatement boundStatement = new BoundStatement(
				preparedStatements.getTemplesById());
		boundStatement.setString(param.getName(), (String) param.getValue());
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
