package com.temples.in.query_interface.resources;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.temples.in.common_utils.HTTPResponses;
import com.temples.in.data_model.Temple;
import com.temples.in.query_interface.services.ITempleService;

public class TempleResourceTest {

	@Mock
	ITempleService templeService;
	
	@InjectMocks
	TempleResource templeResource;
	
	@Mock
	HttpServletRequest httpServletRequest;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void test_getTemples_OK() {
		
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		List<Temple> temples = new ArrayList<Temple>();
		temples.add(new Temple());
		
		expectHttpServletCalls();

		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(templeService.getTemples()).thenReturn(temples);
		
		Response response = templeResource.getTemples(httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.OK);
		
		verify(templeService, times(1)).getTemples();
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
		verify(httpServletRequest, times(1)).getParameterMap();
	}
	
	@Test
	public void test_getTemples_NoContent() {
		
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		List<Temple> temples = new ArrayList<Temple>();
		
		expectHttpServletCalls();

		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(templeService.getTemples()).thenReturn(temples);
		
		Response response = templeResource.getTemples(httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.NO_CONTENT);
		
		verify(templeService, times(1)).getTemples();
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
		verify(httpServletRequest, times(1)).getParameterMap();
	}
	
	@Test
	public void test_getTemples_Filtered_OK() {
		
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] values = {"krishna"};
		parameterMap.put("god", values);
		List<Temple> temples = new ArrayList<Temple>();
		temples.add(new Temple());
		
		expectHttpServletCalls();

		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(templeService.getFilteredTemples(parameterMap)).thenReturn(temples);
		
		Response response = templeResource.getTemples(httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.OK);
		
		verify(templeService, times(1)).getFilteredTemples(parameterMap);
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
		verify(httpServletRequest, times(1)).getParameterMap();
	}
	
	@Test
	public void test_getTemples_Filtered_NoContent() {
		
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] values = {"krishna"};
		parameterMap.put("god", values);
		List<Temple> temples = new ArrayList<Temple>();
		
		expectHttpServletCalls();

		when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
		when(templeService.getFilteredTemples(parameterMap)).thenReturn(temples);
		
		Response response = templeResource.getTemples(httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.NO_CONTENT);
		
		verify(templeService, times(1)).getFilteredTemples(parameterMap);
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
		verify(httpServletRequest, times(1)).getParameterMap();
	}
	
	@Test
	public void test_getTemple_Filtered_OK() {
		
		String id = "id1";
		Temple temple = new Temple();
		
		temple.setState("karnataka");
		temple.setDistrict("udupi");
		temple.setPlace("udupi");
		temple.setGod("krishna");
		temple.setId(id);
		
		expectHttpServletCalls();
		when(templeService.getTemple(id)).thenReturn(temple);
		
		Response response = templeResource.getTemple(id, httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.OK);

		verify(templeService, times(1)).getTemple(id);
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
	}
	
	@Test
	public void test_getTemple_Filtered_NoContent() {
		
		String id = "id1";
		
		expectHttpServletCalls();
		when(templeService.getTemple(id)).thenReturn(null);
		
		Response response = templeResource.getTemple(id, httpServletRequest);

		assertNotNull(response);
		Assert.assertEquals(response.getStatus(), HTTPResponses.NO_CONTENT);

		verify(templeService, times(1)).getTemple(id);
		verify(httpServletRequest, times(1)).getRequestURL();
		verify(httpServletRequest, times(1)).getRemoteAddr();
	}


	private void expectHttpServletCalls() {
		StringBuffer requestURL = new StringBuffer("http://localhost:8080/temples/webapi");
		String incomingIP = "127.0.0.1";
		when(httpServletRequest.getRemoteAddr()).thenReturn(incomingIP);
		when(httpServletRequest.getRequestURL()).thenReturn(requestURL);
	}

}
