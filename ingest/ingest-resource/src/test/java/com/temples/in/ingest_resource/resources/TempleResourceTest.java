package com.temples.in.ingest_resource.resources;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Assert;
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
import com.temples.in.ingest_resource.services.ITempleService;
import com.temples.in.ingest_resource.testdata.TestData;
import com.temples.in.ingest_util.BeanConstants;
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TempleResourceTest {

	@Mock
	AbstractApplicationContext context;

	@Autowired
	@InjectMocks
	TempleResource templeResource;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddTemple() {
		String god = "Krishna";
		String place = "Udupi";
		Temple temple =  Mockito.mock(Temple.class);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		UriBuilder uriBuilder = Mockito.mock(UriBuilder.class);
		Temple newTemple = Mockito.mock(Temple.class);
		ITempleService service = Mockito.mock(ITempleService.class);
		URI uri = TestData.getURI();

		when(temple.getPlace()).thenReturn(place);
		when(temple.getGod()).thenReturn(god);

		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
		when(uriBuilder.path(place + "/" + god))
				.thenReturn(uriBuilder);
		when(uriBuilder.build()).thenReturn(uri);
		when(context.getBean(BeanConstants.TEMPLE_SERVICE)).thenReturn(service);
		when(service.addTemple(temple)).thenReturn(newTemple);

		Response response = templeResource.addTemple(temple, uriInfo, request,
				null);

		Assert.assertNotNull(response);

		verify(temple, times(1)).getPlace();
		verify(temple, times(1)).getGod();
		verify(request, times(1)).getRemoteAddr();
		verify(uriInfo, times(1)).getAbsolutePathBuilder();
		verify(uriBuilder, times(1)).path(
				place + "/" + god);
		verify(uriBuilder, times(1)).build();
		verify(context, times(1)).getBean(BeanConstants.TEMPLE_SERVICE);
		verify(service, times(1)).addTemple(temple);
	}

}
