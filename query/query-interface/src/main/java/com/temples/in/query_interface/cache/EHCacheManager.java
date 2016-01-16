package com.temples.in.query_interface.cache;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.query_data.IDataLoader;
import com.temples.in.query_util.BeanConstants;

public class EHCacheManager implements ApplicationContextAware {

	private Cache templesCahce;
	
	public Cache getTemplesCahce() {
		return templesCahce;
	}

	private CacheManager cacheManager;
	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory
			.getLogger(EHCacheManager.class);


	public EHCacheManager(){
		LOGGER.debug("Creating application cache instance...");
		this.cacheManager = CacheManager.newInstance();
		this.templesCahce = cacheManager.getCache("templescache");
	}

	public void init() {
		LOGGER.info("Inititializing application cache...");
		IDataLoader dataLoader = (IDataLoader) context.getBean(BeanConstants.TEMPLE_DATA_LOADER);
		List<BaseEntity> entityList = dataLoader.getAll();
		for (BaseEntity baseEntity : entityList) {
			templesCahce.put(new Element(baseEntity.getId(), baseEntity));
		}
	}

	public void destroy() {
		this.cacheManager.shutdown();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context =	(AbstractApplicationContext) applicationContext;
	}

}
