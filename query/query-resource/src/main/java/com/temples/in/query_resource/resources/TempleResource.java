package com.temples.in.query_resource.resources;

import java.util.List;

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
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.temples.in.common_utils.LogConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.query_resource.services.ITempleService;
import com.temples.in.query_util.BeanConstants;

@Path("/temples")
@Produces(MediaType.APPLICATION_JSON)
public class TempleResource {
	private AbstractApplicationContext context;
	private ITempleService templeService;
	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleResource.class);

	public TempleResource() {
		LOGGER.debug("Initializing | {}", TempleResource.class.getName());
		try {
			context = new ClassPathXmlApplicationContext(
					BeanConstants.QUERY_RESOURCE_BEAN_FILE);
		} catch (BeansException e) {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Failed to load context | Exception Message={}", e.getMessage());
		}
		context.registerShutdownHook();
		LOGGER.debug("Initialized | {}", TempleResource.class.getName());

	}

	/*
	 * @Context annotation allows you to inject instances of
	 * 
	 * javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.UriInfo,
	 * javax.ws.rs.core.Request, javax.servlet.HttpServletRequest,
	 * javax.servlet.HttpServletResponse, javax.servlet.ServletConfig,
	 * javax.servlet.ServletContext, and javax.ws.rs.core.SecurityContext
	 * objects
	 */

	@GET
	public Response getTemples(@Context HttpServletRequest requestContext) {
		String requestURL = requestContext.getRequestURL().toString();
		String incomingIP = requestContext.getRemoteAddr();
		LOGGER.info(
				"Processing | Request Method=GET | Request URL={} | Remote Host={}",
				requestURL, incomingIP);
		templeService = (ITempleService) context
				.getBean(BeanConstants.TEMPLE_SERVICE);
		List<Temple> templeList = templeService.getTemples();
		GenericEntity<List<Temple>> entity = new GenericEntity<List<Temple>>(
				Lists.newArrayList(templeList)) {
		};
		LOGGER.info(
				"Processed | Request Method=GET | Request URL={} | Remote Host={}",
				requestURL, incomingIP);
		return Response.ok(entity).build();
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
		templeService = (ITempleService) context
				.getBean(BeanConstants.TEMPLE_SERVICE);
		Temple temple = templeService.getTemple(id);
		GenericEntity<Temple> entity = new GenericEntity<Temple>(temple) {
		};
		LOGGER.info(
				"Processed | Request Method=GET | Request URL={} | Path Parameters={} | Remote Host={}",
				requestURL, id, incomingIP);
		return Response.ok(entity).build();
	}
}
