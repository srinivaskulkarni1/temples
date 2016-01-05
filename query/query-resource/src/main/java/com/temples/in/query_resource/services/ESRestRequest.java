package com.temples.in.query_resource.services;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ESRestRequest {

	public static void main(String[] args) {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:9200/temples/_search");

		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}

/*		JsonParser jsonParser = new JsonParser();
		JsonArray results = jsonParser.parse(response.getEntity(String.class)).getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
		
		for (JsonElement jsonElement : results) {
			JsonElement element = jsonElement.getAsJsonObject().get("_source");
			System.out.println(element.toString());
			
		}
			String output = response.getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println(results.toString());*/
	}
}
