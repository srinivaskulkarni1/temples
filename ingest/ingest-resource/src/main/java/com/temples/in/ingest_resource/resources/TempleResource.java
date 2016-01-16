package com.temples.in.ingest_resource.resources;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.temples.in.common_utils.LogConstants;
import com.temples.in.data_model.EntityGroup;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_resource.services.ITempleService;
import com.temples.in.ingest_util.BeanConstants;
import com.temples.in.ingest_util.IDGen;

@Path("/temples")
@Consumes(MediaType.APPLICATION_JSON)
public class TempleResource {
	private AbstractApplicationContext context;
	private static Logger LOGGER = LoggerFactory
			.getLogger(TempleResource.class);

	public TempleResource() {
		LOGGER.debug("Initializing | {}", TempleResource.class.getName());
		try {
			context = new ClassPathXmlApplicationContext(
					BeanConstants.INGEST_RESOURCE_BEAN_FILE);
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

	@POST
	public Response addTemple(Temple temple, @Context UriInfo uriInfo,
			@Context HttpServletRequest requestContext,
			@Context SecurityContext securityContext) {

		String incomingIP = requestContext.getRemoteAddr();
		URI requestURI = uriInfo.getRequestUri();
		String id = IDGen.getCompressedUuid(true);

		LOGGER.info(
				"Processing | Entity Group={}, Entity Id={} | Request Method=POST | URI={} | Remote Host={}",
				EntityGroup.TEMPLES, id, requestURI, incomingIP);
		ITempleService templeService = (ITempleService) context
				.getBean(BeanConstants.TEMPLE_SERVICE);

		temple.setId(id);
		Temple newTemple = templeService.addTemple(temple);
		LOGGER.info(
				"Processed | Entity Group={}, Entity Id={} | Request Method=POST | URI={} | Remote Host={}",
				EntityGroup.TEMPLES, id, requestURI, incomingIP);

		if (newTemple == null) {
			ErrorResponse errorResponse = (ErrorResponse) context.getBean(BeanConstants.ERROR_RESPONSE);
			errorResponse.setErrorCode("500");
			errorResponse.setErrorMessage("Internal exception. Please check log for details");
			errorResponse.setEntity(temple.getClass().getSimpleName());
			errorResponse.setEntityId(id);
			return Response.serverError().entity(errorResponse)
					.build();
		}
		
		URI uri = uriInfo.getAbsolutePathBuilder().path(newTemple.getId())
				.build();

		return Response.created(uri).entity(newTemple).build();

	}
}
