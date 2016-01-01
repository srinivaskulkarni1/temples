package com.temples.in.ingest_resource.services;

import java.util.Map;

import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.Entity;

public interface IQueueManager {

	public abstract boolean enqueue(Action action, Entity entity,
			Map<String, String> pkList);

}