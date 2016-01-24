package com.temples.in.query_interface.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.temples.in.data_model.Temple;
import com.temples.in.query_interface.cache.IEHCacheManager;
//import com.temples.in.query_processor.processor.ESQueryProcessor;

@Component(value="templeservice")
public class TempleService implements ITempleService {

	private static Logger LOGGER = LoggerFactory.getLogger(TempleService.class);

	@Autowired
	@Qualifier("ehcachemanager")
	private IEHCacheManager ehCacheManager;
	
//	@Autowired
//	ESQueryProcessor esQueryProcessor;

	@Override
	public List<Temple> getTemples() {
		LOGGER.debug("Processing {}.getTemples",
				TempleService.class.getSimpleName());

		Map<Object, Element> allElements = ehCacheManager.getTemplesCahce()
				.getAll(ehCacheManager.getTemplesCahce().getKeys());

		List<Temple> temples = new ArrayList<Temple>();

		Iterator<Map.Entry<Object, Element>> entries = allElements.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Map.Entry<Object, Element> entry = entries.next();
			Temple objectValue = (Temple) entry.getValue().getObjectValue();
			temples.add(objectValue);
		}

		LOGGER.debug("Processed {}.getTemples",
				TempleService.class.getSimpleName());

		return temples;
	}

	@Override
	public Temple getTemple(String id) {
		LOGGER.debug("Processing {}.getTemple",
				TempleService.class.getSimpleName());
		Element element = ehCacheManager.getTemplesCahce().get(id);
		LOGGER.debug("Processed {}.getTemple",
				TempleService.class.getSimpleName());

		return (Temple) element.getObjectValue();
	}

	@Override
	public List<Temple> getFilteredTemples(Map<String, String[]> parameterMap) {
		//String id = esQueryProcessor.process(parameterMap);
		return null;
	}

}
