package com.temples.in.query_interface.cache;

import java.util.List;

import com.temples.in.data_model.BaseEntity;

public interface IEHCacheManager {

	public abstract List<? extends BaseEntity> getAll(CacheType caches);

	public abstract BaseEntity getOne(String id, CacheType caches);

	public abstract void put(String id, BaseEntity entity, CacheType caches);
	
	public abstract void init();

	public abstract void destroy();

}