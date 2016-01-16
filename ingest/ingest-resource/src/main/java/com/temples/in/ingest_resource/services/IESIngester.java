package com.temples.in.ingest_resource.services;

import com.temples.in.data_model.BaseEntity;

public interface IESIngester {

	public abstract void postDataToSearcher(String entityId,
			BaseEntity baseEntity) throws RuntimeException;

}