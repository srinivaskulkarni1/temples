package com.temples.in.ingest_resource.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;

import com.temples.in.ingest_data.IDataLoader;
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TempleServiceTest {

	@Mock
	AbstractApplicationContext context;
	
	@Mock
	IDataLoader dataLoader;
	
	@Autowired
	@InjectMocks
	TempleService service;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testGetTempleList() {
/*		List<ITemple> templeList = TestData.getDummyTempleList();
		
		when(context.getBean(BeanConstants.DATA_LOADER)).thenReturn(dataLoader);
		when(dataLoader.getTemples()).thenReturn(templeList);
		
		assertEquals(service.getTemples(), templeList);

		verify(context, times(1)).getBean(BeanConstants.DATA_LOADER);
		verify(dataLoader, timeout(1)).getTemples();
*/	}

}
