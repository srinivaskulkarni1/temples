package com.temples.in.query_data.data_access;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.temples.in.common_utils.ApplicationErrorCodes;
import com.temples.in.common_utils.Configuration;
import com.temples.in.common_utils.LogConstants;
import com.temples.in.data_model.table_info.DBConstants;
import com.temples.in.query_data.Params;
import com.temples.in.query_data.QueryStrings;
import com.temples.in.query_data.exceptions.QueryDataException;

public class DBConnection implements IDBConnection {

	private Cluster cluster;
	private Session session;
	private static Logger LOGGER = LoggerFactory.getLogger(DBConnection.class);
	private final static Integer DEFAULT_MAX_RETRIES = 3;
	private final static Integer DEFAULT_DELAY = 10;
	private static int retryAttempts;
	private static int retryDelay;
	private static String dbHost;

	static {

		retryAttempts = getRetryAttempts(Configuration
				.getProperty(Configuration.DB_CONNECT_RETRY_ATTEMPTS));
		retryDelay = getRetryDelay(Configuration
				.getProperty(Configuration.DB_CONNECT_RETRY_DELAY));
		dbHost = Configuration.getProperty(Configuration.DB_HOST);
	}

	@Override
	public Session getSession() {
		if (session == null) {
			LOGGER.debug("Initializing {}.Session",
					DBConnection.class.getSimpleName());
			connect();
			LOGGER.debug("Initialized {}.Session",
					DBConnection.class.getSimpleName());
		}
		return session;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void connect() {
		int retryCount = 0;
		boolean isConnected = false;

		while (!isConnected && retryCount < retryAttempts) {
			retryCount++;

			LOGGER.info("Database Initialization | Host={} | Keyspace={}",
					dbHost, DBConstants.KEYSPACE);
			cluster = Cluster
					.builder()
					.addContactPoint(dbHost)
					.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
					.withLoadBalancingPolicy(
							new TokenAwarePolicy(new DCAwareRoundRobinPolicy()))
					.build();
			try {
				session = cluster.connect(DBConstants.KEYSPACE);
				isConnected = true;
			} catch (NoHostAvailableException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"NoHostAvailableException thrown | Exception Message={}",
						e.getLocalizedMessage());
			} catch (AuthenticationException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"AuthenticationException thrown | Exception Message={}",
						e.getLocalizedMessage());
			} catch (InvalidQueryException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"InvalidQueryException thrown | Exception Message={}",
						e.getLocalizedMessage());
			} catch (IllegalStateException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"IllegalStateException thrown | Exception Message={}",
						e.getLocalizedMessage());
			}

			if (!isConnected) {

				try {
					Thread.sleep(retryDelay);
				} catch (InterruptedException e) {
					LOGGER.warn("Retry failed | Exception Message={}",
							e.getLocalizedMessage());
				}
			}
		}

		if (isConnected) {
			LOGGER.info("Connected to cluster | Host={} | Keyspace={}", dbHost,
					DBConstants.KEYSPACE);
		}

	}

	private static int getRetryAttempts(String numRetries) {
		int retryAttempts = DEFAULT_MAX_RETRIES;
		if (numRetries != null && numRetries.length() > 0) {
			try {
				retryAttempts = Integer.parseInt(numRetries);
			} catch (NumberFormatException e) {
				LOGGER.warn(
						"Invalid property value | {}={} | expected integer value | defaulting to MAX_RETRIES (3)",
						Configuration.DB_CONNECT_RETRY_ATTEMPTS, numRetries);
			}
		} else {
			LOGGER.warn(
					"Property {} not defined. Defaulting to MAX_RETRIES (3)",
					Configuration.DB_CONNECT_RETRY_ATTEMPTS);
		}
		return retryAttempts;
	}

	private static int getRetryDelay(String retryInSecs) {
		int delay = DEFAULT_DELAY;
		if (retryInSecs != null && retryInSecs.length() > 0) {
			try {
				delay = Integer.parseInt(retryInSecs);
			} catch (NumberFormatException e) {
				LOGGER.warn(
						"Invalid property value | {}={} | expected integer value | Defaulting to DEFAULT_RETRY_DELAY of 10 milliseconds",
						Configuration.DB_CONNECT_RETRY_DELAY, retryInSecs);
			}
		} else {
			LOGGER.warn(
					"Property {} not defined. Defaulting to DEFAULT_RETRY_DELAY of 10 milliseconds",
					Configuration.DB_CONNECT_RETRY_DELAY);
		}
		return delay;
	}

	private void handleConnectionException(int retryAttempts, int retryCount) {
		if (retryCount < retryAttempts) {
			LOGGER.warn(
					"Database connection failed | retrying in | {} | milliseconds",
					retryDelay);
		} else {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Database connection failed. application will now exit...");
			System.exit(0);
		}
	}

	public ResultSet getAll(String statementId) {

		LOGGER.debug("Processing {}.getAll | statement Id={}",
				DBConnection.class.getSimpleName(), statementId);

		ResultSet rs = null;
		Session sessionObj = getSession();

		PreparedStatement statement = QueryStrings
				.getPreparedStatement(statementId);

		if (statement == null) {
			String queryString = getQueryString(statementId);
			statement = createStatement(statementId, queryString);
		}

		BoundStatement boundStatement = new BoundStatement(statement);
		rs = sessionObj.execute(boundStatement);
		LOGGER.debug("Processed {}.getAll | statement Id={}",
				DBConnection.class.getSimpleName(), statementId);
		return rs;

	}

	private PreparedStatement createStatement(String statementId,
			String queryString) {

		try {
			LOGGER.debug(
					"Prepared statement not found in query map for statement Id={}. Creating a new one",
					statementId);
			PreparedStatement statement = session.prepare(queryString);
			QueryStrings.addPreparedStatement(statementId, statement);
			return statement;
		} catch (NoHostAvailableException e) {
			handleNoHostAvailableException(queryString, e);
			return null;
		}
	}

	private String getQueryString(String statementId) {
		String queryString = QueryStrings.getQuery(statementId);

		if (queryString == null) {
			LOGGER.error("No query string defined for statement Id={}",
					statementId);
			throw new QueryDataException(ApplicationErrorCodes.ERR_100,
					"Invalid query string. Cannot process the function call.");
		}
		return queryString;
	}

	public ResultSet getOne(String statementId, List<Params> params) {
		LOGGER.debug("Processing {}.getOne | statement Id={}",
				DBConnection.class.getSimpleName(), statementId);

		ResultSet rs = null;
		Session sessionObj = getSession();

		PreparedStatement statement = QueryStrings
				.getPreparedStatement(statementId);

		String queryString = getQueryString(statementId);
		if (statement == null) {
			statement = createStatement(statementId, queryString);
		}

		BoundStatement boundStatement = new BoundStatement(statement);

		LOGGER.debug("Applying PREDICATE for SELECT statement...");
		StringBuffer boundEntries = new StringBuffer();

		for (Params param : params) {
			String name = param.getName();
			Object value = param.getValue();
			try {
				if (param.getType().equals(String.class)) {
					boundStatement = boundStatement.setString(name,
							(String) value);
					boundEntries.append(name + "=" + value + "|");
				}
			} catch (IllegalArgumentException e) {
				LOGGER.error("Error applying parameter map. SELECT operation aborted | Exception Message={}"
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"IllegalArgumentException | Failed Paramater (name={}, value={})",
						name, value);
				return null;

			} catch (InvalidTypeException e) {
				LOGGER.error("Error applying parameter map. SELECT operation aborted | Exception Message={}"
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"InvalidTypeException | Failed Paramater (name={}, value={})",
						name, value);
				return null;

			}
		}

		LOGGER.debug("PREDICATE for SELECT statement={}",
				boundEntries.toString());
		boundEntries = null;

		try {
			LOGGER.info("Querying for data...");
			rs = sessionObj.execute(boundStatement);
			LOGGER.info("Query executed successfully...");
		} catch (NoHostAvailableException e) {
			handleNoHostAvailableException(queryString, e);
			return null;
		} catch (QueryExecutionException e) {
			LOGGER.error("Query triggered an execution exception. SELECT operation aborted | Exception Message={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryExecutionException | Failed Query={}",
					queryString);
			return null;

		} catch (QueryValidationException e) {
			LOGGER.error("Query syntax is invalid. SELECT operation aborted | Exception Message={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryValidationException | Failed Query={}",
					queryString);
			return null;

		} catch (UnsupportedFeatureException e) {
			LOGGER.error("Feature not supported has been used. SELECT operation aborted | Exception Message={}"
					+ e.getLocalizedMessage());
			LOGGER.debug("UnsupportedFeatureException | Failed Query={}",
					queryString);
			return null;

		}

		LOGGER.debug("Processing {}.getOne | statement Id={}",
				DBConnection.class.getSimpleName(), statementId);
		return rs;

	}

	private void handleNoHostAvailableException(String queryString,
			NoHostAvailableException e) {
		LOGGER.error("No host in the cluster can be contacted successfully to execute this query. SELECT operation aborted | {}"
				+ e.getLocalizedMessage());
		LOGGER.debug("NoHostAvailableException | Failed Query: {}", queryString);
	}
}
