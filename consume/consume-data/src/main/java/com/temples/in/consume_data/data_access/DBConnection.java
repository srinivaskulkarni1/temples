package com.temples.in.consume_data.data_access;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
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
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.MappingManager;
import com.temples.in.common_utils.Configuration;
import com.temples.in.common_utils.LogConstants;
import com.temples.in.consume_data.Params;
import com.temples.in.consume_data.QueryStrings;
import com.temples.in.data_model.table_info.DBConstants;

public class DBConnection implements IDBConnection {

	private Cluster cluster;
	private Session session;
	private MappingManager manager;
	private static Logger LOGGER = LoggerFactory.getLogger(DBConnection.class);
	private final static Integer DEFAULT_MAX_RETRIES = 3;
	private final static Integer DEFAULT_DELAY = 10;
	private static int retryAttempts;
	private static int retryDelay;

	static {

		retryAttempts = getRetryAttempts(Configuration
				.getProperty(Configuration.DB_CONNECT_RETRY_ATTEMPTS));
		retryDelay = getRetryDelay(Configuration
				.getProperty(Configuration.DB_CONNECT_RETRY_DELAY));
	}

	@Override
	public MappingManager getManager() {
		return manager;
	}

	@Override
	public Session getSession() {
		if (session == null) {
			LOGGER.debug("Initializing {}.Session",
					DBConnection.class.getSimpleName());
			connect();
			LOGGER.debug("Initialized {}.Session",
					DBConnection.class.getSimpleName());
			LOGGER.debug("Adding Prepared Statement TEMPLE_SELECT_ONE to cache...");
			QueryStrings.addPreparedStatement(QueryStrings.TEMPLE_SELECT_ONE,
					session.prepare(QueryStrings.TEMPLE_SELECT_QUERY));
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

			LOGGER.info("Initializing database on host | {} | Keyspace | {}",
					"localhost", DBConstants.KEYSPACE);
			cluster = Cluster
					.builder()
					.addContactPoint("localhost")
					.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
					.withLoadBalancingPolicy(
							new TokenAwarePolicy(new DCAwareRoundRobinPolicy()))
					.build();
			try {
				session = cluster.connect(DBConstants.KEYSPACE);
				isConnected = true;
				manager = new MappingManager(session);
			} catch (NoHostAvailableException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"NoHostAvailableException thrown | Exception Message | {}",
						e.getLocalizedMessage());
			} catch (AuthenticationException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"AuthenticationException thrown | Exception Message | {}",
						e.getLocalizedMessage());
			} catch (InvalidQueryException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"InvalidQueryException thrown | Exception Message | {}",
						e.getLocalizedMessage());
			} catch (IllegalStateException e) {
				handleConnectionException(retryAttempts, retryCount);
				LOGGER.debug(
						"IllegalStateException thrown | Exception Message | {}",
						e.getLocalizedMessage());
			}

			if (!isConnected) {

				try {
					Thread.sleep(retryDelay);
				} catch (InterruptedException e) {
					LOGGER.warn("Retry failed | {}", e.getLocalizedMessage());
				}
			}
		}

		if (isConnected) {
			LOGGER.info("Connected to cluster on host | {} | Keyspace | {}",
					"localhost", DBConstants.KEYSPACE);
		}

	}

	private static int getRetryAttempts(String numRetries) {
		int retryAttempts = DEFAULT_MAX_RETRIES;
		if (numRetries != null && numRetries.length() > 0) {
			try {
				retryAttempts = Integer.parseInt(numRetries);
			} catch (NumberFormatException e) {
				LOGGER.warn(
						"Invalid property value | {} | for property | {} | expected integer value | defaulting to MAX_RETRIES (3)",
						numRetries, Configuration.DB_CONNECT_RETRY_ATTEMPTS);
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
						"Invalid property value | {} | for property | {} | expected integer value | Defaulting to DEFAULT_RETRY_DELAY of 10 milliseconds",
						retryInSecs, Configuration.DB_CONNECT_RETRY_DELAY);
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
					"Database connection failed. application will exit");
			System.exit(0);
		}
	}

	public ResultSet getAll(String tableName) {
		LOGGER.info("Processing {}.getAll | table | {}",
				DBConnection.class.getSimpleName(), tableName);
		Statement select = QueryBuilder.select().all().from(tableName);
		ResultSet rs;
		rs = getSession().execute(select);
		LOGGER.info("Processed {}.getAll | table | {}",
				DBConnection.class.getSimpleName(), tableName);
		return rs;

	}

	public ResultSet getOne(String statementId, String queryString,
			List<Params> params) {
		LOGGER.debug("Processing {}.getOne | Query String | {}",
				DBConnection.class.getSimpleName(), queryString);

		ResultSet rs = null;
		Session sessionObj = getSession();

		PreparedStatement statement = QueryStrings
				.getPreparedStatement(statementId);

		if (statement == null) {
			try {
				LOGGER.debug("Prepared statement {} not found in cache. Creating a new one");
				statement = sessionObj.prepare(queryString);
				QueryStrings.addPreparedStatement(statementId, statement);
			} catch (NoHostAvailableException e) {
				handleNoHostAvailableException(queryString, e);
				return null;
			}
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
				LOGGER.error("Error applying parameter map. SELECT operation aborted | {} "
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"IllegalArgumentException | Failed paramater | name={} | value={}",
						name, value);
				return null;

			} catch (InvalidTypeException e) {
				LOGGER.error("Error applying parameter map. SELECT operation aborted | {} "
						+ e.getLocalizedMessage());
				LOGGER.debug(
						"InvalidTypeException | Failed paramater | name={} | value={}",
						name, value);
				return null;

			}
		}

		LOGGER.debug("PREDICATE for SELECT statement |{}",
				boundEntries.toString());
		boundEntries = null;

		try {
			LOGGER.debug("Execute SELECT statement...");
			rs = sessionObj.execute(boundStatement);
		} catch (NoHostAvailableException e) {
			handleNoHostAvailableException(queryString, e);
			return null;
		} catch (QueryExecutionException e) {
			LOGGER.error("Query triggered an execution exception. SELECT operation aborted | {}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryExecutionException | Failed Query: {}",
					queryString);
			return null;

		} catch (QueryValidationException e) {
			LOGGER.error("Query syntax is invalid. Insert operation aborted | {}"
					+ e.getLocalizedMessage());
			LOGGER.debug("QueryValidationException | Failed Query: {}",
					queryString);
			return null;

		} catch (UnsupportedFeatureException e) {
			LOGGER.error("Feature not supported has been used. Insert operation aborted | {}"
					+ e.getLocalizedMessage());
			LOGGER.debug("UnsupportedFeatureException | Failed Query: {}",
					queryString);
			return null;

		}

		LOGGER.debug("Processed {}.getOne | Query String | {}",
				DBConnection.class.getSimpleName(), queryString);
		return rs;

	}

	private void handleNoHostAvailableException(String queryString,
			NoHostAvailableException e) {
		LOGGER.error("No host in the cluster can be contacted successfully to execute this query. SELECT operation aborted | {}"
				+ e.getLocalizedMessage());
		LOGGER.debug("NoHostAvailableException | Failed Query: {}", queryString);
	}
}
