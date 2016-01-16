package com.temples.in.query_interface;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.temples.in.query_interface.cache.EHCacheManager;
import com.temples.in.query_interface.queue.QueueProcessor;
import com.temples.in.query_util.BeanConstants;

public class ApplicationContextListner implements ServletContextListener{
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		EHCacheManager ehCacheManager = (EHCacheManager) webApplicationContext.getBean(BeanConstants.EH_CACHE_MANAGER);
		ehCacheManager.init();
		QueueProcessor queueProcessor = (QueueProcessor) webApplicationContext.getBean(BeanConstants.QUEUE_PROCESSOR);
		queueProcessor.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		EHCacheManager ehCacheManager = (EHCacheManager) webApplicationContext.getBean(BeanConstants.EH_CACHE_MANAGER);
		ehCacheManager.destroy();
		QueueProcessor queueProcessor = (QueueProcessor) webApplicationContext.getBean(BeanConstants.QUEUE_PROCESSOR);
		queueProcessor.destroy();
	}
}
