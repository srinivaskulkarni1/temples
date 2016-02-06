package com.temples.in.ingest_interface;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.temples.in.ingest_interface.services.QueuePublisher;
import com.temples.in.ingest_util.BeanConstants;

public class ApplicationContextListner implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		QueuePublisher queuePublisher = (QueuePublisher) webApplicationContext.getBean(BeanConstants.QUEUE_PUBLISHER);
		queuePublisher.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		QueuePublisher queuePublisher = (QueuePublisher) webApplicationContext.getBean(BeanConstants.QUEUE_PUBLISHER);
		queuePublisher.destroy();
	}
}
