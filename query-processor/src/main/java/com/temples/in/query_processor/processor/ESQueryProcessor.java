package com.temples.in.query_processor.processor;

import java.util.ArrayList;
import java.util.List;

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
import com.google.gson.JsonParser;
import com.temples.in.data_model.Temple;
import com.temples.in.query_processor.builder.ESQuery;
import com.temples.in.query_processor.builder.Results;


public class ESQueryProcessor
{

	private static Logger LOGGER = LoggerFactory.getLogger(ESQueryProcessor.class);
	private String esURL = "http://localhost:9200";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getAll(String resource, Class clazz){
		String query = new ESQuery.Builder().path(esURL).path(resource).path(Results.ALL).build();
		LOGGER.info("Processing | Searcher Query URL={}", query);
		
		LOGGER.debug("Creating Client...");
		Client client = ClientBuilder.newClient();
		LOGGER.debug("Creating WebTarget...");
		WebTarget webTarget = client.target(query);

		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		LOGGER.debug("Invoking get request...");
		Response response = invocationBuilder.get();

		if (response.getStatus() != 200) {
			LOGGER.warn("Failed | Searcher Query URL={}", query);
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}
		
		LOGGER.info("Parsing searcher query response...");
		JsonParser jsonParser = new JsonParser();
		Object entity = response.readEntity(String.class);
		JsonArray results = jsonParser.parse((String) entity).getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
		
		List<Object> list = new ArrayList<Object>();
		Gson gson = new Gson();
		
		for (JsonElement jsonElement : results) {
			JsonElement element = jsonElement.getAsJsonObject().get("_source");
			list.add(gson.fromJson(element, clazz));
		}
		
		LOGGER.debug("Processed | Searcher Query URL={}", query);		
		return list;
	}

	public static void main(String[] args) {
		ESQueryProcessor p = new ESQueryProcessor();
		List<Temple> list = p.getAll("temples", Temple.class);
	}
}
