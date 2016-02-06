package com.temples.in.query_interface.exception_mappers.cacher;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.sf.ehcache.CacheException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temples.in.common_utils.ErrorCodes;

@Provider
public class CacheExceptionMapper implements ExceptionMapper<CacheException>{
	
	private static Logger LOGGER = LoggerFactory
			.getLogger(CacheExceptionMapper.class);

	@Override
	public Response toResponse(CacheException ex) {
		LOGGER.error("Internal exception while processing | Error Code={} | Exception Type={} | Exception Message={}", ErrorCodes.illegalStateError, ex.getClass().getName(), ex.getLocalizedMessage());
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ErrorCodes.cacheError);
		errorResponse.setErrorMessage("Internal exception. Exception Type: " + ex.getClass().getName() + ", Exception Message: " + ex.getLocalizedMessage());
		errorResponse.setEntity("Unknown");
		errorResponse.setEntityId("Unknown");
		return Response.serverError().entity(errorResponse)
				.build();
	}

}
