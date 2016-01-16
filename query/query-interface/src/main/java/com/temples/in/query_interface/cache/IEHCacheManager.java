package com.temples.in.query_interface.cache;

import net.sf.ehcache.Cache;

public interface IEHCacheManager {

	public abstract Cache getTemplesCahce();

	public abstract void init();

	public abstract void destroy();

}