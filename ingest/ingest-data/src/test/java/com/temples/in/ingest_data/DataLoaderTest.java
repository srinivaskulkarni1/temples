package com.temples.in.ingest_data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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

import com.datastax.driver.core.Session;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_data.data_access.IDBConnection;
import com.temples.in.ingest_data.data_access.IInsertStatementExecutor;
import com.temples.in.ingest_util.BeanConstants;

@RunWith(MockitoJUnitRunner.class)
public class DataLoaderTest {

	@Mock
	AbstractApplicationContext context;

	@Autowired
	@InjectMocks
	DataLoader dataLoader;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddTemple() {

		IDBConnection dbConnection = Mockito.mock(IDBConnection.class);
		IInsertStatementExecutor insertStatementExecutor = Mockito
				.mock(IInsertStatementExecutor.class);
		IParamsBulder paramsBuilder = Mockito.mock(IParamsBulder.class);
		Temple temple = Mockito.mock(Temple.class);
		List<Params> paramsList = Mockito.mock(List.class);
		Session session = Mockito.mock(Session.class);

		when(context.getBean(BeanConstants.DB_CONNECTION)).thenReturn(
				dbConnection);
		when(context.getBean(BeanConstants.INSERT_STMT_EXECUTOR)).thenReturn(
				insertStatementExecutor);
		when(context.getBean(BeanConstants.PARAMS_BUILDER)).thenReturn(
				paramsBuilder);
		when(dbConnection.getSession()).thenReturn(session);
		when(paramsBuilder.buildTempleParams(temple)).thenReturn(paramsList);
		when(
				insertStatementExecutor.executeInsert(session,
						QueryStrings.TEMPLE_INSERT_QUERY, paramsList))
				.thenReturn(true);

		assertEquals(dataLoader.addTemple(temple), temple);

		verify(context, times(1)).getBean(BeanConstants.DB_CONNECTION);
		verify(context, times(1)).getBean(BeanConstants.INSERT_STMT_EXECUTOR);
		verify(context, times(1)).getBean(BeanConstants.PARAMS_BUILDER);
		verify(dbConnection, times(1)).getSession();
		verify(paramsBuilder, times(1)).buildTempleParams(temple);
		verify(insertStatementExecutor, times(1)).executeInsert(session,
				QueryStrings.TEMPLE_INSERT_QUERY, paramsList);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddTemple_Null() {

		IDBConnection dbConnection = Mockito.mock(IDBConnection.class);
		IInsertStatementExecutor insertStatementExecutor = Mockito
				.mock(IInsertStatementExecutor.class);
		IParamsBulder paramsBuilder = Mockito.mock(IParamsBulder.class);
		Temple temple = Mockito.mock(Temple.class);
		List<Params> paramsList = Mockito.mock(List.class);
		Session session = Mockito.mock(Session.class);

		when(context.getBean(BeanConstants.DB_CONNECTION)).thenReturn(
				dbConnection);
		when(context.getBean(BeanConstants.INSERT_STMT_EXECUTOR)).thenReturn(
				insertStatementExecutor);
		when(context.getBean(BeanConstants.PARAMS_BUILDER)).thenReturn(
				paramsBuilder);
		when(dbConnection.getSession()).thenReturn(session);
		when(paramsBuilder.buildTempleParams(temple)).thenReturn(paramsList);
		when(
				insertStatementExecutor.executeInsert(session,
						QueryStrings.TEMPLE_INSERT_QUERY, paramsList))
				.thenReturn(false);

		assertEquals(dataLoader.addTemple(temple), null);

		verify(context, times(1)).getBean(BeanConstants.DB_CONNECTION);
		verify(context, times(1)).getBean(BeanConstants.INSERT_STMT_EXECUTOR);
		verify(context, times(1)).getBean(BeanConstants.PARAMS_BUILDER);
		verify(dbConnection, times(1)).getSession();
		verify(paramsBuilder, times(1)).buildTempleParams(temple);
		verify(insertStatementExecutor, times(1)).executeInsert(session,
				QueryStrings.TEMPLE_INSERT_QUERY, paramsList);
	}

}
