package com.temples.in.data_model;

public abstract class BaseEntity {
	private String id;

	public BaseEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
