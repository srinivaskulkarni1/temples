package com.temples.in.query_interface.exception_mappers.cacher;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temples.in.query_util.ErrorCodes;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>{
	
	private static Logger LOGGER = LoggerFactory
			.getLogger(IllegalArgumentExceptionMapper.class);

	@Override
	public Response toResponse(IllegalArgumentException ex) {
		LOGGER.error("Internal exception while processing | Error Code={} | Exception Type={} | Exception Message={}", ErrorCodes.IllegalStateError, ex.getClass().getName(), ex.getLocalizedMessage());
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ErrorCodes.IllegalArgumentError);
		errorResponse.setErrorMessage("Internal exception. Exception Type: " + ex.getClass().getName() + ", Exception Message: " + ex.getLocalizedMessage());
		errorResponse.setEntity("Unknown");
		errorResponse.setEntityId("Unknown");
		return Response.serverError().entity(errorResponse)
				.build();
	}

}
