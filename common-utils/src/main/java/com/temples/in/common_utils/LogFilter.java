package com.temples.in.common_utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (event.getLoggerName().contains("com.temples.in")) {
			return FilterReply.ACCEPT;
		} else {
			return FilterReply.DENY;
		}
	}
}