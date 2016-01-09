package com.temples.in.ingest_util;

public class NullPrimaryKeyException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String msg;
	
	public NullPrimaryKeyException(String message){
		this.setMsg(message);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
