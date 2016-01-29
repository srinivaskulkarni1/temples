package com.temples.in.queue_listener.exceptions;

public class QueueProcessingException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String errorCode;
	
	public QueueProcessingException(String errorCode, String errorMsg) {
		super(errorMsg);
		this.setErrorCode(errorCode);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
