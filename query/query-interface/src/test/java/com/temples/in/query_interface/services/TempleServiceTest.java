package com.temples.in.query_interface.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.temples.in.cacher.CacheType;
import com.temples.in.cacher.IEHCacheManager;
import com.temples.in.data_model.Temple;

public class TempleServiceTest {

	@Mock
	private IEHCacheManager ehCacheManager;
	
	@InjectMocks
	private TempleService templeService;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_getTemples() {
		List<Temple> templeList = templeService.getTemples();
		assertNotNull(templeList);
	}
	
	@Test
	public void test_getTemple_NotNull() {
		String id = "id1";
		Temple t = new Temple();
		t.setId(id);
		when(ehCacheManager.getOne(id, CacheType.Temples)).thenReturn(t);
		Temple temple = templeService.getTemple(id);
		verify(ehCacheManager, times(1)).getOne(id, CacheType.Temples);
		assertNotNull(temple);
	}
	
	@Test
	public void test_getTemple_Null() {
		String id = "id1";
		Temple temple = templeService.getTemple(id);
		Assert.assertNull(temple);
	}
	
	@Test
	public void test_getFilteredTemples() {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();

		List<Temple> templeList = templeService.getFilteredTemples(parameterMap);
		Assert.assertNull(templeList);
	}

}
