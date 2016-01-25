package com.temples.in.cacher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

@Component(value = "ehcachemanager")
public class EHCacheManager implements IEHCacheManager {

	private Cache templesCahce;

	@Autowired
	@Qualifier("templedataloader")
	IDataLoader dataLoader;

	private CacheManager cacheManager;

	private static Logger LOGGER = LoggerFactory
			.getLogger(EHCacheManager.class);

	public EHCacheManager() {
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

	@SuppressWarnings("rawtypes")
	@Override
	public List<? extends BaseEntity> getAll(CacheType caches) {

		List<BaseEntity> entityList = new ArrayList<BaseEntity>();

		if (CacheType.Temples.equals(caches)) {

			List keys = templesCahce.getKeys();

			if (keys != null && keys.size() > 0) {
				Map<Object, Element> allElements = templesCahce.getAll(keys);

				Iterator<Map.Entry<Object, Element>> entries = allElements
						.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<Object, Element> entry = entries.next();
					BaseEntity objectValue = (BaseEntity) entry.getValue()
							.getObjectValue();
					entityList.add(objectValue);
				}
			}
		}

		return entityList;
	}

	@Override
	public void put(String id, BaseEntity entity, CacheType caches) {
		if (CacheType.Temples.equals(caches)) {
			templesCahce.put(new Element(id, entity));
		}
	}

	@Override
	public BaseEntity getOne(String id, CacheType caches) {
		if (CacheType.Temples.equals(caches)) {

			Element element = templesCahce.get(id);
			if (element != null) {
				return (BaseEntity) element.getObjectValue();
			}
		}
		return null;
	}
}
