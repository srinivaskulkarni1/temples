package com.temples.in.ingest_client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestRequest {

	private static BufferedReader read;
	private static Logger LOGGER = LoggerFactory.getLogger(RestRequest.class);

	public void postRestRequest(String jsonFilePath, String ingestURL) {
		LOGGER.info("Reading Json file | {}", jsonFilePath);
		LOGGER.info("Request Method | POST, Request URL | {}", ingestURL);
		try {
			read = new BufferedReader(new FileReader(jsonFilePath));

			String line = read.readLine();
			Client client = Client.create();
			WebResource webResource = client.resource(ingestURL);
			int successCount = 0;
			int failureCount = 0;

			while (true) {
				LOGGER.info("Processing entry | {}", line);

				ClientResponse response = webResource.type(
						MediaType.APPLICATION_JSON).post(ClientResponse.class,
						line);

				if (response.getStatus() != 201) {
					failureCount++;
					LOGGER.error("POST request failed | {} ", line);
				} else {
					successCount++;
				}

				/*
				 * System.out.println("Output from Server .... \n"); String
				 * output = response.getEntity(String.class);
				 * System.out.println(output)
				 */

				if ((line = read.readLine()) != null) {// if not last line
					continue;
				} else {
					// if last line
					break;
				}
			}
			read.close();
			LOGGER.info("Completed POST request | Success | {} | Failure | {}",
					successCount, failureCount);

		} catch (FileNotFoundException e) {
			LOGGER.error(
					"FileNotFoundException occured while processing file | {} | Exception message | {}",
					jsonFilePath, e.getLocalizedMessage());

			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error(
					"IOException occured while processing file | {} | Exception message | {}",
					jsonFilePath, e.getLocalizedMessage());

			e.printStackTrace();
		}

	}
}
