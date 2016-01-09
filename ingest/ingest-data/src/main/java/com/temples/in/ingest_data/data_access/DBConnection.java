package com.temples.in.ingest_data.data_access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.temples.in.common_utils.Configuration;
import com.temples.in.common_utils.LogConstants;
import com.temples.in.data_model.table_info.DBConstants;

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
					"Database connection failed | retrying in {} milliseconds",
					retryDelay);
		} else {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Database connection failed. application will exit");
			System.exit(0);
		}
	}
}
