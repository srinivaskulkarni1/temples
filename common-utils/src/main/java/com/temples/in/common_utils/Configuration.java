package com.temples.in.common_utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class Configuration extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertiesMap;
	private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	public static final String DB_CONNECT_RETRY_ATTEMPTS = "db.connect.retry.attempts";
	public static final String DB_CONNECT_RETRY_DELAY = "db.connect.retry.delay";
	public static final String QUEUE_HOST = "queue.host";
	public static final String INGEST_EXCHANGE = "queue.exchange";
	public static final String INGEST_ROUTING_KEY = "queue.routingkey";
	public static final String CLIENT_INGEST_URL = "client.ingest_url";
	public static final String DB_HOST = "db.host";

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

}