package com.temples.in.ingest_interface.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.temples.in.common_utils.ApplicationConfiguration;
import com.temples.in.common_utils.Conversions;
import com.temples.in.data_model.BaseEntity;

@Component(value = "searchpublisher")
public class SearchPublisher implements ISearchPublisher {

	@Autowired
	private ApplicationConfiguration configuration;

	private static Logger LOGGER = LoggerFactory
			.getLogger(SearchPublisher.class);

	@Override
	public void postDataToSearcher(String entityId, BaseEntity baseEntity)
			throws RuntimeException {

		LOGGER.debug("Entity Id={} | Processing {}.postDataToSearcher",
				entityId, SearchPublisher.class.getSimpleName());

		String templeJson = Conversions.getJsonFromEntity(baseEntity);
		String requestURL = configuration.getElasticSearchURL();

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
					+ "Failed to post data to searcher | HTTP error code : "
					+ response.getStatus());
		}

		LOGGER.info("Entity Id={} | Successfully posted data to searcher",
				entityId);
		LOGGER.debug("Entity Id={}  Processed {}.postDataToSearcher", entityId,
				SearchPublisher.class.getSimpleName());
	}
}
