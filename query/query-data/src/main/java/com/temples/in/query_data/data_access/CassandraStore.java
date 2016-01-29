package com.temples.in.query_data.data_access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.temples.in.common_utils.ApplicationConfiguration;
import com.temples.in.query_data.exceptions.QueryDataException;
import com.temples.in.query_util.ErrorCodes;

@Component
@Qualifier(value = "cassandrastore")
public class CassandraStore implements IDBConnection {

	private Cluster cluster;
	private Session session;
	private static Logger LOGGER = LoggerFactory
			.getLogger(CassandraStore.class);

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
					CassandraStore.class.getSimpleName());
			connect();
			LOGGER.debug("Initialized {}.Session",
					CassandraStore.class.getSimpleName());
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
				.withQueryOptions(queryOptions)
				.withSocketOptions(socketOptions)
				.withRetryPolicy(DefaultRetryPolicy.INSTANCE)
				.withLoadBalancingPolicy(
						new TokenAwarePolicy(DCAwareRoundRobinPolicy.builder()
								.withLocalDc(cassandraDC)
								.withUsedHostsPerRemoteDc(0).build())).build();

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
}
