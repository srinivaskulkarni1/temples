package com.temples.in.ingest_resource.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.data_model.Temple;
import com.temples.in.ingest_data.data_loader.IDataLoader;
import com.temples.in.ingest_util.BeanConstants;
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TempleServiceTest {

	@Mock
	AbstractApplicationContext context;
	
	@Autowired
	@InjectMocks
	TempleService service;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testAddTemple_NullTempleReturn() {
		
		Temple temple =  Mockito.mock(Temple.class);
		IDataLoader dataLoader = Mockito.mock(IDataLoader.class);

	//	when(dataLoader.addTemple(temple)).thenReturn(null);
		when(context.getBean(BeanConstants.TEMPLE_DATA_LOADER)).thenReturn(dataLoader);

		assertEquals(service.addTemple(temple), null);

		verify(context, times(1)).getBean(BeanConstants.TEMPLE_DATA_LOADER);
	//	verify(dataLoader, times(1)).addTemple(temple);
	}
	
	@Test
	public void testAddTemple_NullTempleReturn_QueueFailed() {

		String god = "krishna";
		String place = "udupi";
		Temple temple = Mockito.mock(Temple.class);
		IDataLoader dataLoader = Mockito.mock(IDataLoader.class);
		Temple newTemple = Mockito.mock(Temple.class);
		IQueueManager queueManager = Mockito.mock(IQueueManager.class);

		when(context.getBean(BeanConstants.TEMPLE_DATA_LOADER)).thenReturn(dataLoader);
	//	when(dataLoader.addTemple(temple)).thenReturn(newTemple);
		when(newTemple.getGod()).thenReturn(god);
		when(newTemple.getPlace()).thenReturn(place);
		when(context.getBean(BeanConstants.QUEUE_MANAGER)).thenReturn(
				queueManager);

		assertEquals(service.addTemple(temple), null);

		verify(context, times(1)).getBean(BeanConstants.TEMPLE_DATA_LOADER);
		verify(context, times(1)).getBean(BeanConstants.QUEUE_MANAGER);
	//	verify(dataLoader, times(1)).addTemple(temple);
		verify(newTemple, times(1)).getGod();
		verify(newTemple, times(1)).getPlace();
	}

}
