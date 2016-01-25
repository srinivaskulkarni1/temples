package com.temples.in.query_interface.resources;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.Temple;
import com.temples.in.query_interface.services.ITempleService;

@Path("/temples")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class TempleResource {

	@Autowired
	@Qualifier("templeservice")
	private ITempleService templeService;

	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleResource.class);

	/*
	 * @Context annotation allows you to inject instances of
	 * 
	 * javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.UriInfo,
	 * javax.ws.rs.core.Request, javax.servlet.HttpServletRequest,
	 * javax.servlet.HttpServletResponse, javax.servlet.ServletConfig,
	 * javax.servlet.ServletContext, and javax.ws.rs.core.SecurityContext
	 * objects
	 */

	@SuppressWarnings("unchecked")
	@GET
	public Response getTemples(@Context HttpServletRequest requestContext) {
		String requestURL = requestContext.getRequestURL().toString();
		String incomingIP = requestContext.getRemoteAddr();
		Map<String, String[]> parameterMap = requestContext.getParameterMap();

		LOGGER.info(
				"Processing | Request Method=GET | Request URL={} | Remote Host={}",
				requestURL, incomingIP);

		List<Temple> templeList = null;
		if (parameterMap.size() > 0) {
			templeList = templeService.getFilteredTemples(parameterMap);
		} else {
			templeList = templeService.getTemples();
		}

		Response res;
		
		if(templeList.size() == 0){
			res = Response.noContent().build();
		}else{
			GenericEntity<List<Temple>> entity = new GenericEntity<List<Temple>>(
					Lists.newArrayList(templeList)) {
			};
			res = Response.ok(entity).build();
		}
		
		LOGGER.info(
				"Processed | Request Method=GET | Request URL={} | Remote Host={}",
				requestURL, incomingIP);
		return res;
	}

	@GET
	@Path("/{Id}")
	public Response getTemple(@PathParam("Id") String id,
			@Context HttpServletRequest requestContext) {
		String requestURL = requestContext.getRequestURL().toString();
		String incomingIP = requestContext.getRemoteAddr();
		LOGGER.info(
				"Processing | Request Method=GET | Request URL={} | Path Parameters={} | Remote Host={}",
				requestURL, id, incomingIP);
		Temple temple = templeService.getTemple(id);
		
		Response res;
		
		if(temple == null){
			res = Response.noContent().build();
		}else{
			GenericEntity<Temple> entity = new GenericEntity<Temple>(temple) {
			};
			res = Response.ok(entity).build();
		}
		LOGGER.info(
				"Processed | Request Method=GET | Request URL={} | Path Parameters={} | Remote Host={}",
				requestURL, id, incomingIP);
		return res;
	}
}
