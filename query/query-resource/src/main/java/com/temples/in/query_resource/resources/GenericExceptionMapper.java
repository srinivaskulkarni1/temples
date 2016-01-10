package com.temples.in.query_resource.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable>{
	
	private static Logger LOGGER = LoggerFactory
			.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable ex) {
		LOGGER.error("Internal exception while processing | Exception Type={} | Exception Message={}", ex.getClass().getName(), ex.getLocalizedMessage());
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode("500");
		errorResponse.setErrorMessage("Internal exception. Exception Type: " + ex.getClass().getName() + ", Exception Message: " + ex.getLocalizedMessage());
		errorResponse.setEntity("Unknown");
		errorResponse.setEntityId("Unknown");
		return Response.serverError().entity(errorResponse)
				.build();
	}

}
