package com.temples.in.queue_listener;

import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.queue_listener.exceptions.QueueProcessingException;

public interface IMessageProcessor {

	public abstract boolean process(String entityId, EntityInfo entityInfo)
			throws QueueProcessingException;

}