package com.temples.in.common_utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration extends PropertyPlaceholderConfigurer {

	private static Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

	private static Map<String, String> propertiesMap;
	private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	public static final String CONFIG_RABBITMQ_HOST = "rabbitmq.host";
	public static final String CONFIG_RABBITMQ_EXCHANGE = "rabbitmq.exchange";
	public static final String CONFIG_RABBITMQ_ROUTING_KEY = "rabbitmq.routingkey";
	public static final String CLIENT_INGEST_URL = "client.ingest_url";
	public static final String CONFIG_ELASTIC_SEARCH_URL = "elasticsearch.url";

	public static final String CONFIG_SEED_NODES = "cassandrastore.seednodes";

	public static final String CONFIG_PORT = "cassandrastore.port";
	private static final Integer DEFAULT_PORT = 9042;
	
	public static final String CONFIG_DEFAULT_CONSISTENCY_LEVEL = "cassandrastore.consistencylevel";
	private static final String DEFAULT_CONSISTENCY_LEVEL = "ONE";

	public static final String CONFIG_READ_TIMEOUT = "cassandrastore.readtimeout";
	private static final  Integer DEFAULT_READ_TIMEOUT = 30000;

	public static final String CONFIG_USERNAME = "cassandrastore.username";
	public static final String CONFIG_PASSWORD = "cassandrastore.password";
	public static final String CONFIG_KEYSPACE = "cassandrastore.keyspace";
	
	public static final String CONFIG_DC = "cassandrastore.datacenter";
	private static final String DEFAULT_DC = "datacenter1";



	@Override
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		super.setSystemPropertiesMode(systemPropertiesMode);
		springSystemPropertiesMode = systemPropertiesMode;
	}

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props)
			throws BeansException {
		super.processProperties(beanFactory, props);

		propertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String valueStr = resolvePlaceholder(keyStr, props,
					springSystemPropertiesMode);
			propertiesMap.put(keyStr, valueStr);
		}
	}

	public static String getProperty(String name) {
		if (propertiesMap.containsKey(name)) {
			return propertiesMap.get(name);
		}
		return null;
	}
	
	public int getCassandraReadTimeout() {
		
		int timeout = DEFAULT_READ_TIMEOUT;
		String readTimeout = getProperty(CONFIG_READ_TIMEOUT);
		
		if (readTimeout != null && readTimeout.length() > 0) {
			try {
				timeout = Integer.parseInt(readTimeout);
			} catch (NumberFormatException e) {
				LOGGER.warn(
						"Invalid property value | {}={} | expected integer value | defaulting to DEFAULT_READ_TIMEOUT (30000 Millis)",
						CONFIG_READ_TIMEOUT, readTimeout);
				timeout = DEFAULT_READ_TIMEOUT;
			}
		} else {
			LOGGER.debug(
					"Property {} not defined. Defaulting to DEFAULT_READ_TIMEOUT (30000 Millis)",
					CONFIG_READ_TIMEOUT);
		}
		
		return timeout;
	}
	

	public String getCassandraConsistencyLevel() {
		String level = getProperty(CONFIG_DEFAULT_CONSISTENCY_LEVEL);
		if (level != null && level.length() > 0) {
			return level;
		}else{
			LOGGER.debug(
					"Property {} not defined. Defaulting to DEFAULT_CONSISTENCY_LEVEL (ConsostencyLevel.ONE)",
					CONFIG_DEFAULT_CONSISTENCY_LEVEL);
			return DEFAULT_CONSISTENCY_LEVEL;
		}
	}
	
	public String[] getCassandraSeedNodes() {
		String seedNodes = getProperty(CONFIG_SEED_NODES);
		String[] seedNodeList = null;
		if (seedNodes != null && seedNodes.length() > 0) {
			if(seedNodes.contains(",")){
				seedNodeList = seedNodes.split(",");
			}else{
				seedNodeList = new String[]{seedNodes}; 
			}
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to cassandra store",
					CONFIG_SEED_NODES);
		}
		
		return seedNodeList;
	}
	
	public int getCassandraPort() {
		
		Integer defaultPort = DEFAULT_PORT;
		String port = getProperty(CONFIG_PORT);
		
		if (port != null && port.length() > 0) {
			try {
				defaultPort = Integer.parseInt(port);
			} catch (NumberFormatException e) {
				LOGGER.warn(
						"Invalid property value | {}={} | expected integer value | defaulting to DEFAULT_PORT (9042)",
						CONFIG_PORT, port);
				defaultPort = DEFAULT_PORT;
			}
		} else {
			LOGGER.debug(
					"Property {} not defined. Defaulting to DEFAULT_PORT (9042)",
					CONFIG_PORT);
		}
		
		return defaultPort;
	}
	
	public String getCassandraUserName() {
		String userName = getProperty(CONFIG_USERNAME);
		if (userName != null && userName.length() > 0) {
			return userName;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to cassandra store",
					CONFIG_USERNAME);
		}
		return null;
	}

	public String getCassandraPassword() {
		String password = getProperty(CONFIG_PASSWORD);
		if (password != null && password.length() > 0) {
			return password;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to cassandra store",
					CONFIG_PASSWORD);
		}
		return null;
	}
	
	public String getCassandraKeyspace() {
		String keyspace = getProperty(CONFIG_KEYSPACE);
		if (keyspace != null && keyspace.length() > 0) {
			return keyspace;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to cassandra store",
					CONFIG_KEYSPACE);
		}
		return null;
	}
	
	public String getCassandraDC() {
		String dc = getProperty(CONFIG_DC);
		if (dc != null && dc.length() > 0) {
			return dc;
		}else{
			LOGGER.debug(
					"Property {} not defined. Defaulting to DEFAULT_DC (datacenter1)",
					CONFIG_DC);
			return DEFAULT_DC;
		}
	}
	
	public String getRabbitMqHost() {
		String rabbitMqHost = getProperty(CONFIG_RABBITMQ_HOST);
		if (rabbitMqHost != null && rabbitMqHost.length() > 0) {
			return rabbitMqHost;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to rabbit mq",
					CONFIG_RABBITMQ_HOST);
		}
		return null;
	}
	
	public String getRabbitMqExchange() {
		String exchange = getProperty(CONFIG_RABBITMQ_EXCHANGE);
		if (exchange != null && exchange.length() > 0) {
			return exchange;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to rabbit mq",
					CONFIG_RABBITMQ_EXCHANGE);
		}
		return null;
	}
	
	public String getRabbitMqRoutingKey() {
		String routingKey = getProperty(CONFIG_RABBITMQ_ROUTING_KEY);
		if (routingKey != null && routingKey.length() > 0) {
			return routingKey;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot connect to rabbit mq",
					CONFIG_RABBITMQ_ROUTING_KEY);
		}
		return null;
	}

	public String getElasticSearchURL() {
		String esURL = getProperty(CONFIG_ELASTIC_SEARCH_URL);
		if (esURL != null && esURL.length() > 0) {
			return esURL;
		}else{
			LOGGER.error(
					"Property {} not defined. Cannot publish data to searcher",
					CONFIG_ELASTIC_SEARCH_URL);
		}
		return null;
	}
}