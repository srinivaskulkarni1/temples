package com.temples.in.ingest_data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
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
import com.temples.in.ingest_util.BeanConstants;

@RunWith(MockitoJUnitRunner.class)
public class ParamsBuilderTest {
	
	@Mock
	AbstractApplicationContext context;

	@Autowired
	@InjectMocks
	ParamsBuilder paramsBuilder;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testBuildTempleParams_AllParams(){
		String place = "Udupi";
		String district = "Udupi";
		String god = "Krishna";
		String state = "Karnataka";
		
		Temple temple = Mockito.mock(Temple.class);
		Params params1 = Mockito.mock(Params.class);
		Params params2 = Mockito.mock(Params.class);
		Params params3 = Mockito.mock(Params.class);
		Params params4 = Mockito.mock(Params.class);
		
		when(temple.getGod()).thenReturn(god);
		when(temple.getPlace()).thenReturn(place);
		when(temple.getDistrict()).thenReturn(district);
		when(temple.getState()).thenReturn(state);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params1);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params2);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params3);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params4);
		
		params1.setType(god.getClass());
		params1.setName(DBConstants.TABLE_TEMPLE_GOD);
		params1.setValue(god);
		
		params2.setType(place.getClass());
		params2.setName(DBConstants.TABLE_TEMPLE_PLACE);
		params2.setValue(place);
		
		params3.setType(district.getClass());
		params3.setName(DBConstants.TABLE_TEMPLE_DISTRICT);
		params3.setValue(district);
		
		params4.setType(state.getClass());
		params4.setName(DBConstants.TABLE_TEMPLE_STATE);
		params4.setValue(state);
		
		List<Params> paramsList = paramsBuilder.buildTempleParams(temple);
		Assert.assertNotNull(paramsList);
		assertEquals(paramsList.size(), 4);

		verify(temple, times(1)).getGod();
		verify(temple, times(1)).getPlace();
		verify(temple, times(1)).getDistrict();
		verify(temple, times(1)).getState();
		verify(context, times(4)).getBean(BeanConstants.PARAMS);
		
	}
	
	@Test
	public void testBuildTempleParams_NoParams(){
		
		Temple temple = Mockito.mock(Temple.class);
		
		when(temple.getGod()).thenReturn(null);
		when(temple.getPlace()).thenReturn(null);
		when(temple.getDistrict()).thenReturn(null);
		when(temple.getState()).thenReturn(null);
		
		List<Params> paramsList = paramsBuilder.buildTempleParams(temple);
		Assert.assertNotNull(paramsList);
		assertEquals(paramsList.size(), 0);

		verify(temple, times(1)).getGod();
		verify(temple, times(1)).getPlace();
		verify(temple, times(1)).getDistrict();
		verify(temple, times(1)).getState();
		verify(context, times(0)).getBean(BeanConstants.PARAMS);
		
	}
	
	@Test
	public void testBuildTempleParams_NoDistrict(){
		String place = "Udupi";
		String god = "Krishna";
		String state = "Karnataka";
		
		Temple temple = Mockito.mock(Temple.class);
		Params params1 = Mockito.mock(Params.class);
		Params params2 = Mockito.mock(Params.class);
		Params params4 = Mockito.mock(Params.class);
		
		when(temple.getGod()).thenReturn(god);
		when(temple.getPlace()).thenReturn(place);
		when(temple.getDistrict()).thenReturn(null);
		when(temple.getState()).thenReturn(state);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params1);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params2);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params4);
		
		params1.setType(god.getClass());
		params1.setName(DBConstants.TABLE_TEMPLE_GOD);
		params1.setValue(god);
		
		params2.setType(place.getClass());
		params2.setName(DBConstants.TABLE_TEMPLE_PLACE);
		params2.setValue(place);
		
		params4.setType(state.getClass());
		params4.setName(DBConstants.TABLE_TEMPLE_STATE);
		params4.setValue(state);
		
		List<Params> paramsList = paramsBuilder.buildTempleParams(temple);
		Assert.assertNotNull(paramsList);
		assertEquals(paramsList.size(), 3);

		verify(temple, times(1)).getGod();
		verify(temple, times(1)).getPlace();
		verify(temple, times(1)).getDistrict();
		verify(temple, times(1)).getState();
		verify(context, times(3)).getBean(BeanConstants.PARAMS);
		
	}
	
	@Test
	public void testBuildTempleParams_NoState(){
		String place = "Udupi";
		String district = "Udupi";
		String god = "Krishna";
		
		Temple temple = Mockito.mock(Temple.class);
		Params params1 = Mockito.mock(Params.class);
		Params params2 = Mockito.mock(Params.class);
		Params params3 = Mockito.mock(Params.class);
		
		when(temple.getGod()).thenReturn(god);
		when(temple.getPlace()).thenReturn(place);
		when(temple.getDistrict()).thenReturn(district);
		when(temple.getState()).thenReturn(null);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params1);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params2);
		when(context.getBean(BeanConstants.PARAMS)).thenReturn(params3);
		
		params1.setType(god.getClass());
		params1.setName(DBConstants.TABLE_TEMPLE_GOD);
		params1.setValue(god);
		
		params2.setType(place.getClass());
		params2.setName(DBConstants.TABLE_TEMPLE_PLACE);
		params2.setValue(place);
		
		params3.setType(district.getClass());
		params3.setName(DBConstants.TABLE_TEMPLE_DISTRICT);
		params3.setValue(district);
		
		List<Params> paramsList = paramsBuilder.buildTempleParams(temple);
		Assert.assertNotNull(paramsList);
		assertEquals(paramsList.size(), 3);

		verify(temple, times(1)).getGod();
		verify(temple, times(1)).getPlace();
		verify(temple, times(1)).getDistrict();
		verify(temple, times(1)).getState();
		verify(context, times(3)).getBean(BeanConstants.PARAMS);
		
	}
}
