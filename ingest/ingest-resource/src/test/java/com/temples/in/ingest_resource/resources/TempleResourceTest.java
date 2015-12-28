package com.temples.in.ingest_resource.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.temples.in.ingest_resource.services.ITempleService;
import com.temples.in.ingest_resource.testdata.TestData;
import com.temples.in.ingest_util.BeanConstants;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TempleResourceTest {

	@Mock
	AbstractApplicationContext context;
	
	@Mock
	ITempleService service;
	
	@Autowired
	@InjectMocks
	TempleResource templeResource;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testGetTemples(){
/*		List<ITemple> templeList = TestData.getDummyTempleList();
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		
		when(context.getBean(BeanConstants.TEMPLE_SERVICE)).thenReturn(service);
		when(service.getTemples()).thenReturn(templeList);
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		
		assertEquals(templeResource.getTemples(null, request, null), templeList);

		verify(context, times(1)).getBean(BeanConstants.TEMPLE_SERVICE);
		verify(service, times(1)).getTemples();
		verify(request, times(1)).getRemoteAddr();*/
	}
	
}
