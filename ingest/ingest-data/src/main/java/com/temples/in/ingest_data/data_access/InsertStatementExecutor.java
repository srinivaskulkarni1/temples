package com.temples.in.ingest_data.data_access;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import com.temples.in.ingest_data.Params;
import com.temples.in.ingest_data.ParamsBuilder;

public class InsertStatementExecutor implements IInsertStatementExecutor {

	private static Logger LOGGER = LoggerFactory.getLogger(ParamsBuilder.class);

	@Override
	public boolean executeInsert(String id, Session session, String queryString,
			List<Params> params) {

		LOGGER.debug("Processing | Id={} | {}.executeInsert",
				id, InsertStatementExecutor.class.getSimpleName());

		PreparedStatement statement;

		try {
			statement = session.prepare(queryString);
		} catch (NoHostAvailableException e) {
			return handleNoHostAvailableException(queryString, e);
		}
		BoundStatement boundStatement = new BoundStatement(statement);

		for (Params param : params) {
			try {
				if (param.getType().equals(String.class)) {
					boundStatement = boundStatement.setString(param.getName(),
							(String) param.getValue());
				}
			} catch (IllegalArgumentException e) {
				LOGGER.error("Error applying parameter map. Insert operation aborted | Exception Mesasge={}"
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"IllegalArgumentException | Failed paramater | name={} | value={}",
						param.getName(), param.getValue());
				return false;

			} catch (InvalidTypeException e) {
				LOGGER.error("Error applying parameter map. Insert operation aborted | Exception Mesasge={}"
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"InvalidTypeException | Failed paramater | name={} | value={}",
						param.getName(), param.getValue());
				return false;

			}
		}

		try {
			session.execute(boundStatement);
		} catch (NoHostAvailableException e) {
			return handleNoHostAvailableException(queryString, e);
		} catch (QueryExecutionException e) {
			LOGGER.error("Query triggered an execution exception. Insert operation aborted | Exception Mesasge={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryExecutionException | Failed Query={}",
					queryString);
			return false;

		} catch (QueryValidationException e) {
			LOGGER.error("Query syntax is invalid. Insert operation aborted | Exception Mesasge={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryValidationException | Failed Query={}",
					queryString);
			return false;

		} catch (UnsupportedFeatureException e) {
			LOGGER.error("Feature not supported has been used. Insert operation aborted | Exception Mesasge={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("UnsupportedFeatureException | Failed Query={}",
					queryString);
			return false;

		}

		LOGGER.debug("Processed | Id={} | {}.executeInsert",
				id, InsertStatementExecutor.class.getSimpleName());

		return true;
	}

	private boolean handleNoHostAvailableException(String queryString,
			NoHostAvailableException e) {
		LOGGER.error("No host in the cluster can be contacted successfully to execute this query. Insert operation aborted | Exception Mesasge={}"
				+ e.getLocalizedMessage());
		LOGGER.debug("NoHostAvailableException | Failed Query={}", queryString);
		return false;
	}
}
