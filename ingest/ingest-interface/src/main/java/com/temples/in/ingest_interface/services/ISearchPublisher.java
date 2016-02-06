package com.temples.in.ingest_interface.services;

import com.temples.in.data_model.BaseEntity;

public interface ISearchPublisher {

	public abstract void postDataToSearcher(String entityId,
			BaseEntity baseEntity) throws RuntimeException;

}