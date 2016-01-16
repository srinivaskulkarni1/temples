package com.temples.in.query_data;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.PreparedStatement;
import com.temples.in.data_model.table_info.DBConstants;

public class QueryStrings {

	public static final String TEMPLE_SELECT_ONE_QUERY = "SELECT" + " "
			+ DBConstants.ID + "," + DBConstants.ENTITY + " " + "FROM" + " "
			+ DBConstants.TABLE_TEMPLE + " " + "WHERE" + " " + DBConstants.ID
			+ "=" + "?" + ";";

	public static final String TEMPLE_SELECT_ALL_QUERY = "SELECT" + " "
			+ DBConstants.ID + "," + DBConstants.ENTITY + " " + "FROM" + " "
			+ DBConstants.TABLE_TEMPLE + ";";

	public static final String TEMPLE_SELECT_ONE = "TEMPLE_SELECT_ONE";

	public static final String TEMPLE_SELECT_ALL = "TEMPLE_SELECT_ALL";

	private static Map<String, PreparedStatement> preparedStmtMap = new HashMap<String, PreparedStatement>();

	private static Map<String, String> queryMap = new HashMap<String, String>();

	static{
		queryMap.put(TEMPLE_SELECT_ONE, TEMPLE_SELECT_ONE_QUERY);
		queryMap.put(TEMPLE_SELECT_ALL, TEMPLE_SELECT_ALL_QUERY);
	}
	
	public static String getQuery(String statementId) {
		if (queryMap.containsKey(statementId)) {
			return queryMap.get(statementId);
		}
		return null;
	}	
	
	public static void addPreparedStatement(String statementId,
			PreparedStatement stmt) {
		if (!preparedStmtMap.containsKey(statementId)) {
			preparedStmtMap.put(statementId, stmt);
		}
	}

	public static PreparedStatement getPreparedStatement(String statementId) {
		if (preparedStmtMap.containsKey(statementId)) {
			return preparedStmtMap.get(statementId);
		}
		return null;
	}
}
