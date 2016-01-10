package com.temples.in.common_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPResponses {
	private static Logger LOGGER = LoggerFactory.getLogger(HTTPResponses.class);

	public static final int OK = 200;
	public static final int CREATED = 201;
	public static final int ACCEPTED = 202;
	public static final int NO_CONTENT = 204;
	public static final int RESET_CONTENT = 205;
	public static final int PARTIAL_CONTENT = 206;
	public static final int MOVED_PERMANENTLY = 301;
	public static final int FOUND = 302;
	public static final int SEE_OTHER = 303;
	public static final int NOT_MODIFIED = 304;
	public static final int USE_PROXY = 305;
	public static final int TEMPORARY_REDIRECT = 307;
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int PAYMENT_REQUIRED = 402;;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int METHOD_NOT_ALLOWED = 405;
	public static final int NOT_ACCEPTABLE = 406;
	public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
	public static final int REQUEST_TIMEOUT = 408;
	public static final int CONFLICT = 409;
	public static final int GONE = 410;
	public static final int LENGTH_REQUIRED = 411;
	public static final int PRECONDITION_FAILED = 412;
	public static final int REQUEST_ENTITY_TOO_LARGE = 413;
	public static final int REQUEST_URI_TOO_LONG = 414;
	public static final int UNSUPPORTED_MEDIA_TYPE = 415;
	public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
	public static final int EXPECTATION_FAILED = 417;
	public static final int INTERNAL_SERVER_ERROR = 500;
	public static final int NOT_IMPLEMENTED = 501;
	public static final int BAD_GATEWAY = 502;
	public static final int SERVICE_UNAVAILABLE = 503;
	public static final int GATEWAY_TIMEOUT = 504;

	public static void handleResponse(int response) {
		if (response >= HTTPResponses.MOVED_PERMANENTLY
				&& response <= HTTPResponses.TEMPORARY_REDIRECT) {
			LOGGER.error(
					"Content Redirection status code returned | HTTP STATUS CODE={}",
					response);
			LOGGER.debug("Throwing a runtime exception. There is no support to handle this status code");
			throw new RuntimeException(
					"Content Redirection | HTTP STATUS CODE=" + response);
		} else if (response >= HTTPResponses.BAD_REQUEST
				&& response <= HTTPResponses.EXPECTATION_FAILED) {
			LOGGER.error(
					"Client Error status code returned | HTTP STATUS CODE={}",
					response);
			LOGGER.debug("Throwing a runtime exception. The request cannot be satisfied");
			throw new RuntimeException("Client Error | HTTP STATUS CODE="
					+ response);

		} else if (response >= HTTPResponses.INTERNAL_SERVER_ERROR
				&& response <= HTTPResponses.GATEWAY_TIMEOUT) {

			LOGGER.error(
					"Server Error status code returned | HTTP STATUS CODE={}",
					response);
			LOGGER.debug("Throwing a runtime exception. Server failed to satisfy the request");
			throw new RuntimeException("Server Error | HTTP STATUS CODE="
					+ response);

		}
	}
}
