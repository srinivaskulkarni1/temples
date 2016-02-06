package com.temples.in.query_interface.exception_mappers.cacher;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temples.in.common_utils.ErrorCodes;

@Provider
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException>{
	
	private static Logger LOGGER = LoggerFactory
			.getLogger(IllegalStateExceptionMapper.class);

	@Override
	public Response toResponse(IllegalStateException ex) {
		LOGGER.error("Internal exception while processing | Error Code={} | Exception Type={} | Exception Message={}", ErrorCodes.illegalStateError, ex.getClass().getName(), ex.getLocalizedMessage());
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ErrorCodes.illegalStateError);
		errorResponse.setErrorMessage("Internal exception. Exception Type: " + ex.getClass().getName() + ", Exception Message: " + ex.getLocalizedMessage());
		errorResponse.setEntity("Unknown");
		errorResponse.setEntityId("Unknown");
		return Response.serverError().entity(errorResponse)
				.build();
	}

}
