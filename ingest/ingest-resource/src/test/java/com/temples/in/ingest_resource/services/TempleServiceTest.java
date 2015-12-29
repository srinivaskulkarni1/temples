package com.temples.in.ingest_resource.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
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
import com.temples.in.ingest_data.IDataLoader;
import com.temples.in.ingest_util.BeanConstants;
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
	public void testAddTemple() {
		
		Temple temple =  Mockito.mock(Temple.class);
		IDataLoader dataLoader = Mockito.mock(IDataLoader.class);
		Temple newTemple = Mockito.mock(Temple.class);

		when(dataLoader.addTemple(temple)).thenReturn(newTemple);
		when(context.getBean(BeanConstants.DATA_LOADER)).thenReturn(dataLoader);

		assertEquals(service.addTemple(temple), newTemple);

		verify(context, times(1)).getBean(BeanConstants.DATA_LOADER);
		verify(dataLoader, times(1)).addTemple(temple);
	}

}
