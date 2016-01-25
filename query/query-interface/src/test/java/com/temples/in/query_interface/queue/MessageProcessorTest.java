package com.temples.in.query_interface.queue;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.Action;
import com.temples.in.data_model.wrapper.EntityInfo;
import com.temples.in.data_model.wrapper.EntityType;
import com.temples.in.data_model.wrapper.PrimaryKey;
import com.temples.in.query_data.IDataLoader;
import com.temples.in.query_interface.cache.CacheType;
import com.temples.in.query_interface.cache.IEHCacheManager;

public class MessageProcessorTest {

	@Mock
	private IDataLoader dataLoader;

	@Mock
	private IEHCacheManager ehCacheManager;
	
	@Mock
	private EntityInfo entityInfo;
	
	@Mock
	private PrimaryKey primaryKey;
	
	@Mock
	Temple temple;
	
	@InjectMocks
	private MessageProcessor messageProcessor;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void test_process_temple_post_NotNull() throws Exception {
		when(entityInfo.getAction()).thenReturn(Action.POST);
		when(entityInfo.getEntityType()).thenReturn(EntityType.TEMPLE);
		when(entityInfo.getPrimaryKey()).thenReturn(primaryKey);
		when(dataLoader.getOne(primaryKey)).thenReturn(temple);
		
		messageProcessor.process("id1", entityInfo);
		InOrder inOrder = inOrder(entityInfo, dataLoader, ehCacheManager);
		
		inOrder.verify(entityInfo, times(1)).getAction();
		inOrder.verify(entityInfo, times(1)).getEntityType();
		inOrder.verify(entityInfo, times(1)).getPrimaryKey();
		inOrder.verify(dataLoader, times(1)).getOne(primaryKey);
		inOrder.verify(ehCacheManager, times(1)).put("id1", temple, CacheType.Temples);
	}
	
	@Test
	public void test_process_temple_post_Null() throws Exception {
		when(entityInfo.getAction()).thenReturn(Action.POST);
		when(entityInfo.getEntityType()).thenReturn(EntityType.TEMPLE);
		when(entityInfo.getPrimaryKey()).thenReturn(primaryKey);
		when(dataLoader.getOne(primaryKey)).thenReturn(null);
		
		messageProcessor.process("id1", entityInfo);	
		
		InOrder inOrder = inOrder(entityInfo, dataLoader);
		
		inOrder.verify(entityInfo, times(1)).getAction();
		inOrder.verify(entityInfo, times(1)).getEntityType();
		inOrder.verify(entityInfo, times(1)).getPrimaryKey();
		inOrder.verify(dataLoader, times(1)).getOne(primaryKey);

	}
	
	@Test
	public void test_process_entity_post() throws Exception {
		when(entityInfo.getAction()).thenReturn(Action.POST);
		when(entityInfo.getEntityType()).thenReturn(null);
		
		messageProcessor.process("id1", entityInfo);
		
		InOrder inOrder = inOrder(entityInfo);
		
		inOrder.verify(entityInfo, times(1)).getAction();
		inOrder.verify(entityInfo, times(1)).getEntityType();

	}
	
	@Test(expected = Exception.class)
	public void test_process_entity_put() throws Exception {
		when(entityInfo.getAction()).thenReturn(Action.PUT);
			messageProcessor.process("id1", entityInfo);
	}
	
	@Test(expected = Exception.class)
	public void test_process_entity_delete() throws Exception {
		when(entityInfo.getAction()).thenReturn(Action.DELETE);
			messageProcessor.process("id1", entityInfo);
	}
	
	@Test(expected = Exception.class)
	public void test_process_entity_unsupported_action() throws Exception {
		when(entityInfo.getAction()).thenReturn(null);
			messageProcessor.process("id1", entityInfo);
	}
	
	@Test(expected = Exception.class)
	public void test_process_entityInfo_null() throws Exception {
			messageProcessor.process("id1", null);
	}

}
