package com.temples.in.query_processor.processor;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.temples.in.common_utils.HTTPResponses;
import com.temples.in.data_model.Temple;
import com.temples.in.query_processor.builder.ESQuery;
import com.temples.in.query_processor.builder.Results;

public class ESQueryProcessor {

	private static Logger LOGGER = LoggerFactory
			.getLogger(ESQueryProcessor.class);
	private String esURL = "http://localhost:9200";

	public <T> List<T> getAll(String resource, Class<T> clazz) {
		LOGGER.debug("Processing {}.getAll",
				ESQueryProcessor.class.getSimpleName());
		String query = new ESQuery.Builder().baseURL(esURL).path(resource)
				.searchCriteria(Results.ALL).build();
		LOGGER.info("Processing | Searcher Query URL={}", query);

		LOGGER.debug("Creating Client...");
		Client client = ClientBuilder.newClient();
		LOGGER.debug("Creating WebTarget...");
		WebTarget webTarget = client.target(query);

		Invocation.Builder invocationBuilder = webTarget
				.request(MediaType.APPLICATION_JSON);
		LOGGER.debug("Invoking get request...");
		Response response;
		try {
			response = invocationBuilder.get();
		} catch (ProcessingException ex) {
			LOGGER.error(
					"ProcessingException while executing query={} | Exception Message=",
					query, ex.getLocalizedMessage());
			throw new RuntimeException(
					"ProcessingException while executing query" + query);
		}

		handleESResponse(response);

		LOGGER.debug("Parsing query results...");

		JsonParser jsonParser = new JsonParser();
		Object entity = response.readEntity(String.class);
		JsonArray results = null;

		LOGGER.debug("Response from searcher:");
		LOGGER.debug(entity.toString());
		try {
			results = jsonParser.parse((String) entity).getAsJsonObject()
					.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
		} catch (JsonSyntaxException ex) {
			handleException(JsonSyntaxException.class.getSimpleName(), ex);

		} catch (JsonParseException ex) {
			handleException(JsonParseException.class.getSimpleName(), ex);

		} catch (IllegalStateException ex) {
			handleException(IllegalStateException.class.getSimpleName(), ex);
		}

		LOGGER.info("Query executed successfully | Results returned={}",
				results.size());

		List<T> list = new ArrayList<T>();
		Gson gson = new Gson();

		for (JsonElement jsonElement : results) {
			try {
				JsonElement element = jsonElement.getAsJsonObject().get(
						"_source");
				list.add(gson.fromJson(element, clazz));
			} catch (IllegalStateException ex) {
				handleException(IllegalStateException.class.getSimpleName(), ex);

			} catch (JsonSyntaxException ex) {
				handleException(JsonSyntaxException.class.getSimpleName(), ex);
			}
		}

		LOGGER.debug("Processed {}.getAll",
				ESQueryProcessor.class.getSimpleName());

		return list;
	}

	private void handleException(String exceptionName, Exception ex) {
		LOGGER.error("{} while parsing query results | Exception Message=",
				exceptionName, ex.getLocalizedMessage());
		throw new RuntimeException(exceptionName
				+ " while parsing query results. Exception Message="
				+ ex.getLocalizedMessage());
	}

	private void handleESResponse(Response response) {
		LOGGER.debug("Processing {}.handleESResponse",
				ESQueryProcessor.class.getSimpleName());

		if (response.getStatus() >= HTTPResponses.OK
				&& response.getStatus() <= HTTPResponses.PARTIAL_CONTENT) {
			switch (response.getStatus()) {
			case HTTPResponses.RESET_CONTENT: {
				LOGGER.warn(
						"Reset Content status code returned | Http Status Code={}",
						response.getStatus());
				LOGGER.debug("Throwing runtime exception. This case is not handled");
				throw new RuntimeException(
						"Reset Content status code returned : "
								+ response.getStatus());

			}
			case HTTPResponses.PARTIAL_CONTENT: {
				LOGGER.warn(
						"Partial Content status code returned | Http Status Code={}",
						response.getStatus());
				LOGGER.debug("Throwing runtime exception. This case is not handled");
				throw new RuntimeException(
						"Partial Content status code returned : "
								+ response.getStatus());

			}
			case HTTPResponses.NO_CONTENT: {
				LOGGER.warn(
						"No Content status code returned | Http Status Code={}",
						response.getStatus());
				LOGGER.debug("Throwing runtime exception. There is no content to return to client");
				throw new RuntimeException(
						"There is no content to return to client : "
								+ response.getStatus());

			}
			}

		} else {
			HTTPResponses.handleResponse(response.getStatus());
		}
		LOGGER.debug("Processed {}.handleESResponse",
				ESQueryProcessor.class.getSimpleName());

	}

	public static void main(String[] args) {
		ESQueryProcessor esQueryProcessor = new ESQueryProcessor();
		List<Temple> temples = esQueryProcessor.getAll("temples", Temple.class);
		System.out.println(temples);
	}
}
