package com.temples.in.ingest_interface.resources;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.EntityGroup;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_interface.services.ITempleService;
import com.temples.in.ingest_util.IDGen;

@Path("/temples")
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class TempleResource {

	@Autowired
	@Qualifier("templeservice")
	private ITempleService templeService;

	@Autowired
	private ErrorResponse errorResponse;

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

		temple.setId(id);
		Temple newTemple = templeService.addTemple(temple);
		LOGGER.info(
				"Processed | Entity Group={}, Entity Id={} | Request Method=POST | URI={} | Remote Host={}",
				EntityGroup.TEMPLES, id, requestURI, incomingIP);

		if (newTemple == null) {
			errorResponse.setErrorCode("500");
			errorResponse
					.setErrorMessage("Internal exception. Please check log for details");
			errorResponse.setEntity(temple.getClass().getSimpleName());
			errorResponse.setEntityId(id);
			return Response.serverError().entity(errorResponse).build();
		}

		URI uri = uriInfo.getAbsolutePathBuilder().path(newTemple.getId())
				.build();

		return Response.created(uri).entity(newTemple).build();

	}
}
