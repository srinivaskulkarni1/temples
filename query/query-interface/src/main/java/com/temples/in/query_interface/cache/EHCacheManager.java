package com.temples.in.query_interface.cache;

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.query_data.IDataLoader;

@Component(value="ehcachemanager")
public class EHCacheManager implements IEHCacheManager {

	private Cache templesCahce;
	
	@Autowired
	@Qualifier("templedataloader")
	IDataLoader dataLoader;

	@Override
	public Cache getTemplesCahce() {
		return templesCahce;
	}

	private CacheManager cacheManager;

	private static Logger LOGGER = LoggerFactory
			.getLogger(EHCacheManager.class);


	public EHCacheManager(){
	}

	@Override
	public void init() {
		LOGGER.debug("Creating application cache instance...");
		this.cacheManager = CacheManager.newInstance();
		this.templesCahce = cacheManager.getCache("templescache");
		LOGGER.info("Inititializing application cache...");
		List<BaseEntity> entityList = dataLoader.getAll();
		for (BaseEntity baseEntity : entityList) {
			templesCahce.put(new Element(baseEntity.getId(), baseEntity));
		}
	}

	@Override
	public void destroy() {
		this.cacheManager.shutdown();
	}

}
