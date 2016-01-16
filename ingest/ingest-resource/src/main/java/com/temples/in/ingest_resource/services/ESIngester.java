package com.temples.in.ingest_resource.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.temples.in.data_model.BaseEntity;

public class ESIngester implements IESIngester {

	private static Logger LOGGER = LoggerFactory.getLogger(ESIngester.class);

	@Override
	public void postDataToSearcher(String entityId, BaseEntity baseEntity)
			throws RuntimeException {

		LOGGER.debug("Entity Id={} | Processing {}.postDataToSearcher",
				entityId, ESIngester.class.getSimpleName());

		Gson gson = new Gson();
		String templeJson = gson.toJson(baseEntity);
		// to be moved to config
		String requestURL = "http://localhost:9200/temples/temple";

		LOGGER.info("Entity Id={} | Posting data to searcher", entityId);

		LOGGER.debug(
				"Entity Id={} | Posting data to searcher | URL={} | Message={}",
				entityId, requestURL, templeJson);

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(requestURL).path(entityId);
		Invocation.Builder invocationBuilder = webTarget.request();
		Response response = invocationBuilder.post(Entity.json(templeJson));

		if (response.getStatus() != 201) {
			throw new RuntimeException("Entity Id=" + entityId
					+ "Failed to post data to searcher | HTTP ERROR CODE : "
					+ response.getStatus());
		}

		LOGGER.info("Entity Id={} | Successfully posted data to searcher",
				entityId);
		LOGGER.debug("Entity Id={}  Processed {}.postDataToSearcher", entityId,
				ESIngester.class.getSimpleName());
	}
}
