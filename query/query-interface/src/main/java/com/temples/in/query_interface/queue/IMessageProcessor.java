package com.temples.in.query_interface.queue;

import com.temples.in.data_model.wrapper.EntityInfo;

public interface IMessageProcessor {

	public abstract boolean process(String entityId, EntityInfo entityInfo)
			throws Exception;

}