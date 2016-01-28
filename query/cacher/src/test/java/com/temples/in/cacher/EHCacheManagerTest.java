package com.temples.in.cacher;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.Temple;
import com.temples.in.query_data.IDataLoader;

public class EHCacheManagerTest {

	@Mock
	private Ehcache templesCahce;

	@Mock
	private IDataLoader dataLoader;

	@Mock
	private CacheManager cacheManager;

	@Spy @InjectMocks
	private EHCacheManager ehCacheManager;

	@Mock
	private Element element;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_init_emptylist() {
		List<BaseEntity> entityList = new ArrayList<BaseEntity>();
		doReturn(cacheManager).when(ehCacheManager).getCacheManagerInstance();
		when(cacheManager.getEhcache("templescache")).thenReturn(templesCahce);
		when(dataLoader.getAll()).thenReturn(entityList);
		ehCacheManager.init();
		verify(cacheManager, times(1)).getEhcache("templescache");
		verify(dataLoader, times(1)).getAll();
	}
	
	@Test
	public void test_init_listwithvalues() {
		List<BaseEntity> entityList = new ArrayList<BaseEntity>();
		Temple temple = new Temple();
		temple.setId("id1");
		entityList.add(temple);
		doReturn(cacheManager).when(ehCacheManager).getCacheManagerInstance();
		when(cacheManager.getEhcache("templescache")).thenReturn(templesCahce);
		when(dataLoader.getAll()).thenReturn(entityList);
		ehCacheManager.init();
		verify(cacheManager, times(1)).getEhcache("templescache");
		verify(dataLoader, times(1)).getAll();
		verify(templesCahce, times(1)).put(Matchers.any(Element.class));
	}
	
	@Test
	public void test_destroy() {
		ehCacheManager.destroy();
		verify(cacheManager, times(1)).shutdown();
	}
	
	@Test
	public void test_put() {
		Temple temple = new Temple();
		String id = "id1";
		temple.setId(id);
		ehCacheManager.put(id, temple, CacheType.Temples);
		verify(templesCahce, times(1)).put(Matchers.any(Element.class));
	}
	
	@Test
	public void test_invalidcache() {
		Temple temple = new Temple();
		String id = "id1";
		temple.setId(id);
		ehCacheManager.put(id, temple, null);
	}
	
	@Test
	public void test_getOne_notnull() {
		
		BaseEntity temple = Mockito.mock(BaseEntity.class);
		String id = "id1";

		when(templesCahce.get(id)).thenReturn(element);
		doReturn(temple).when(ehCacheManager).getBaseEntity(element);
		
		ehCacheManager.getOne(id, CacheType.Temples);
		
		verify(templesCahce, times(1)).get(id);
	}
	
	@Test
	public void test_getOne_invalidcache() {
		
		String id = "id1";

		BaseEntity one = ehCacheManager.getOne(id, null);
		Assert.assertNull(one);
		
	}
	
	@Test
	public void test_getOne_null() {
		
		String id = "id1";

		when(templesCahce.get(id)).thenReturn(null);
		
		BaseEntity one = ehCacheManager.getOne(id, CacheType.Temples);
		Assert.assertNull(one);
		
		verify(templesCahce, times(1)).get(id);
	}

}
