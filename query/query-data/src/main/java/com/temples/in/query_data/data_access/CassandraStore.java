package com.temples.in.query_data.data_access;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.exceptions.InvalidTypeException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.temples.in.common_utils.ApplicationConfiguration;
import com.temples.in.common_utils.ApplicationErrorCodes;
import com.temples.in.query_data.Params;
import com.temples.in.query_data.QueryStrings;
import com.temples.in.query_data.exceptions.QueryDataException;
import com.temples.in.query_util.ErrorCodes;

@Component
@Qualifier(value = "cassandrastore")
public class CassandraStore implements IDBConnection {

	private Cluster cluster;
	private Session session;
	private static Logger LOGGER = LoggerFactory.getLogger(DBConnection.class);

	private String[] seedNodes;
	private String userName;
	private String password;
	private String keyspace;
	private String consistencyLevel;
	private String cassandraDC;
	private int readTimeout;
	private int cassandraPort;

	@Autowired
	ApplicationConfiguration configuration;

	private void readConfig() {

		LOGGER.info("Reading cassandra configuration properties...");
		seedNodes = configuration.getCassandraSeedNodes();
		if (seedNodes == null) {
			throw new QueryDataException(
					ErrorCodes.dbConnectError,
					"Mandatory configuration paramater '"
							+ ApplicationConfiguration.CONFIG_SEED_NODES
							+ "' is not defined. Cannot connect to cassandra store");
		}

		userName = configuration.getCassandraUserName();

		if (userName == null) {
			throw new QueryDataException(
					ErrorCodes.dbConnectError,
					"Mandatory configuration paramater '"
							+ ApplicationConfiguration.CONFIG_USERNAME
							+ "' is not defined. Cannot connect to cassandra store");
		}

		password = configuration.getCassandraPassword();

		if (password == null) {
			throw new QueryDataException(
					ErrorCodes.dbConnectError,
					"Mandatory configuration paramater '"
							+ ApplicationConfiguration.CONFIG_PASSWORD
							+ "' is not defined. Cannot connect to cassandra store");
		}

		keyspace = configuration.getCassandraKeyspace();

		if (keyspace == null) {
			throw new QueryDataException(
					ErrorCodes.dbConnectError,
					"Mandatory configuration paramater '"
							+ ApplicationConfiguration.CONFIG_KEYSPACE
							+ "' is not defined. Cannot connect to cassandra store");
		}

		consistencyLevel = configuration.getCassandraConsistencyLevel();
		readTimeout = configuration.getCassandraReadTimeout();
		cassandraDC = configuration.getCassandraDC();
		cassandraPort = configuration.getCassandraPort();

		LOGGER.info("Cassandra configuration properties are:");
		LOGGER.info(ApplicationConfiguration.CONFIG_SEED_NODES + "="
				+ seedNodes[0]);
		LOGGER.info(ApplicationConfiguration.CONFIG_PORT + "=" + cassandraPort);
		LOGGER.info(ApplicationConfiguration.CONFIG_USERNAME + "=" + userName);
		LOGGER.info(ApplicationConfiguration.CONFIG_KEYSPACE + "=" + keyspace);
		LOGGER.info(ApplicationConfiguration.CONFIG_DEFAULT_CONSISTENCY_LEVEL
				+ "=" + consistencyLevel);
		LOGGER.info(ApplicationConfiguration.CONFIG_READ_TIMEOUT + "="
				+ readTimeout);
		LOGGER.info(ApplicationConfiguration.CONFIG_DC + "=" + cassandraDC);
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
	public void connect() {

		readConfig();
		QueryOptions queryOptions = new QueryOptions();
		queryOptions.setConsistencyLevel(ConsistencyLevel
				.valueOf(consistencyLevel));

		SocketOptions socketOptions = new SocketOptions();
		socketOptions.setReadTimeoutMillis(readTimeout);

		this.cluster = Cluster
				.builder()
				.addContactPoints(seedNodes)
				.withPort(cassandraPort)
				.withCredentials(userName, password)
				.withLoadBalancingPolicy(
						new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder()
								.withLocalDc(cassandraDC)
								.withUsedHostsPerRemoteDc(0).build()))
				.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
				.withQueryOptions(queryOptions)
				.withSocketOptions(socketOptions).build();

		Metadata metadata = cluster.getMetadata();
		LOGGER.info("Connected to cluster | {}", metadata.getClusterName());

		for (Host host : metadata.getAllHosts()) {
			LOGGER.info("Data Center={} | Host={} | Rack={}",
					host.getDatacenter(), host.getAddress().toString(),
					host.getRack());
		}

		session = cluster.connect();

		session.execute("USE " + keyspace);
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
