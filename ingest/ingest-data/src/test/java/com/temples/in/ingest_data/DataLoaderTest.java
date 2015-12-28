package com.temples.in.ingest_data;


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

import com.temples.in.ingest_data.data_access.IDBConnection;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class DataLoaderTest {

	@Mock
	AbstractApplicationContext context;
	
	@Mock
	IDBConnection dbConnection;

	@Autowired
	@InjectMocks
	DataLoader dataLoader;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetTempleList_null(){
/*		List<ITemple> templeList = new ArrayList<ITemple>();
		ResultSet resultSet = null;
		
		when(context.getBean(BeanConstants.DB_CONNECTION)).thenReturn(dbConnection);
		when(dbConnection.getResultSet(DBConstants.TABLE_TEMPLE)).thenReturn(resultSet);
		
		assertEquals(dataLoader.getTemples(), templeList);
		
		verify(context, times(1)).getBean(BeanConstants.DB_CONNECTION);
		verify(dbConnection, times(1)).getResultSet(DBConstants.TABLE_TEMPLE);
*/	}

	

}
