package com.temples.in.ingest_resource.services;

import java.util.Map;

import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityType;

public interface IQueueManager {

	public abstract boolean enqueue(Action action, EntityType entity,
			Map<String, String> pkList);

}