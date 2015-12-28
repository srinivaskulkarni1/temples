package com.temples.in.ingest_resource.resources;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.temples.in.common_utils.LogConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_resource.services.ITempleService;
import com.temples.in.ingest_util.BeanConstants;

@Path("/temples")
@Consumes(MediaType.APPLICATION_JSON)
public class TempleResource {
	private AbstractApplicationContext context;
	private ITempleService templeService;
	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleResource.class);

	public TempleResource() {
		LOGGER.debug("Initializing | {}", TempleResource.class.getName());
		try {
			context = new ClassPathXmlApplicationContext(
					BeanConstants.INGEST_RESOURCE_BEAN_FILE);
		} catch (BeansException e) {
			LOGGER.error(LogConstants.MARKER_FATAL,
					"Failed to load conext | {}", e.getMessage());
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

	@POST
	public Response addTemple(Temple temple, @Context UriInfo uriInfo,
			@Context HttpServletRequest requestContext,
			@Context SecurityContext securityContext) {

		String incomingIP = requestContext.getRemoteAddr();
		URI uri = uriInfo.getAbsolutePathBuilder()
				.path(temple.getPlace() + "/" + temple.getGod()).build();

		LOGGER.info(
				"Processing | POST | addTemple | Parameters | {},{} | Remote Host | {}",
				temple.getPlace(), temple.getGod(), incomingIP);
		templeService = (ITempleService) context
				.getBean(BeanConstants.TEMPLE_SERVICE);

		Temple newTemple = templeService.addTemple(temple);
		LOGGER.info(
				"Processed | POST | addTemple | Parameters | {},{} | Remote Host | {}",
				temple.getPlace(), temple.getGod(), incomingIP);

		if (newTemple == null) {
			return Response.serverError().status(Status.INTERNAL_SERVER_ERROR)
					.build();
		}

		return Response.created(uri).entity(newTemple).build();

	}
}
