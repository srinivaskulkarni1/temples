package com.temples.in.consume_data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import com.datastax.driver.core.ResultSet;
import com.temples.in.consume_data.TempleDataLoader;
import com.temples.in.consume_data.data_access.IDBConnection;
import com.temples.in.consume_util.BeanConstants;
import com.temples.in.data_model.Temple;
import com.temples.in.data_model.table_info.DBConstants;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class TempleDataLoaderTest {

	@Mock
	AbstractApplicationContext context;
	
	@Mock
	IDBConnection dbConnection;
	
	@Autowired
	@InjectMocks
	TempleDataLoader dataLoader;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
	}
	
/*	@Test
	public void testGetTempleList_null(){
		List<Temple> templeList = new ArrayList<Temple>();
		ResultSet resultSet = null;
		
		when(context.getBean(BeanConstants.DB_CONNECTION)).thenReturn(dbConnection);
		when(dbConnection.getAll(DBConstants.TABLE_TEMPLE)).thenReturn(resultSet);
		
		assertEquals(dataLoader.getTempleList(), templeList);
		
		verify(context, times(1)).getBean(BeanConstants.DB_CONNECTION);
		verify(dbConnection, times(1)).getAll(DBConstants.TABLE_TEMPLE);
	}*/

}
