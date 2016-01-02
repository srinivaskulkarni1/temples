package com.temples.in.ingest_client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.temples.in.common_utils.Configuration;
import com.temples.in.common_utils.LogConstants;

public class IngestClient {
	private static Logger LOGGER = LoggerFactory.getLogger(IngestClient.class);

	public static void main(String args[]) {
		LOGGER.info("******* REST CLIENT *********");
		LOGGER.debug("Loading application context");
		AbstractApplicationContext context;
		try {
			context = new ClassPathXmlApplicationContext(
					"ingest-client-beans.xml");
			context.registerShutdownHook();
		} catch (BeansException e) {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Failed to load conext | {}", e.getMessage());
		}
		LOGGER.info("START - Process csv file and POST requests");

		
		CSVtoJSON parse = new CSVtoJSON();
		String jsonFilePath = parse.convert();
		String ingestURL = Configuration
				.getProperty(Configuration.CLIENT_INGEST_URL);
		RestRequest restRequest = new RestRequest();
		restRequest.postRestRequest(jsonFilePath, ingestURL);
		LOGGER.info("END - Process csv file and POST requests");
		System.exit(0);
	}
}
