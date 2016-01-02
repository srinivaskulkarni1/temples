package com.temples.in.ingest_data.data_access;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class InsertStatementExecutorTest {

	@Autowired
	@InjectMocks
	InsertStatementExecutor insertStatementExecutor;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteInsert(){
/*		List<Params> paramsList = Mockito.mock(List.class);
		Session session = Mockito.mock(Session.class);
		PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
		
		
		//doThrow(NoHostAvailableException.class).when(session).prepare(QueryStrings.TEMPLE_INSERT_QUERY);
		when(session.prepare(QueryStrings.TEMPLE_INSERT_QUERY)).thenReturn(preparedStatement);
		boolean bInsert = insertStatementExecutor.executeInsert(session, QueryStrings.TEMPLE_INSERT_QUERY, paramsList);
		assertEquals(bInsert, false);
		verify(session, times(1)).prepare(QueryStrings.TEMPLE_INSERT_QUERY);
*/	}
	
}
