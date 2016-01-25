package com.temples.in.query_interface.services;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.cacher.CacheType;
import com.temples.in.cacher.IEHCacheManager;
import com.temples.in.data_model.Temple;

@Component(value = "templeservice")
public class TempleService implements ITempleService {

	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	@Autowired
	@Qualifier("ehcachemanager")
	private IEHCacheManager ehCacheManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Temple> getTemples() {
		LOGGER.debug("Processing {}.getTemples",
				TempleService.class.getSimpleName());

		List<Temple> temples = (List<Temple>) ehCacheManager.getAll(CacheType.Temples);

		LOGGER.debug("Processed {}.getTemples",
				TempleService.class.getSimpleName());

		return temples;
	}

	@Override
	public Temple getTemple(String id) {
		LOGGER.debug("Processing {}.getTemple",
				TempleService.class.getSimpleName());
		
		Temple temple = (Temple) ehCacheManager.getOne(id, CacheType.Temples);
		
		LOGGER.debug("Processed {}.getTemple",
				TempleService.class.getSimpleName());
		
		return temple;
	}

	@Override
	public List<Temple> getFilteredTemples(Map<String, String[]> parameterMap) {
		return null;
	}

}
