package com.temples.in.ingest_interface.services;

import java.util.Map;

import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;

public interface IQueuePublisher {

	public abstract boolean enqueue(Action action, EntityType entity,
			Map<String, Object> pkList);

}