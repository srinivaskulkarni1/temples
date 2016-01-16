package com.temples.in.query_data.exceptions;

public class QueryDataException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMsg;
	
	public QueryDataException(String errorCode, String errorMsg) {
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
