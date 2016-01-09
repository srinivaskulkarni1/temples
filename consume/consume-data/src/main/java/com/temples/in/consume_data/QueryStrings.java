package com.temples.in.consume_data;

import java.util.HashMap;
import java.util.Map;

import com.datastax.driver.core.PreparedStatement;
import com.temples.in.data_model.table_info.DBConstants;

public class QueryStrings {

	public static final String TEMPLE_SELECT_QUERY = "SELECT" + " "
			+ DBConstants.ID + ","
			+ DBConstants.TABLE_TEMPLE_GOD + ","
			+ DBConstants.TABLE_TEMPLE_PLACE + ","
			+ DBConstants.TABLE_TEMPLE_DISTRICT + ","
			+ DBConstants.TABLE_TEMPLE_STATE + " " + "FROM" + " "
			+ DBConstants.TABLE_TEMPLE + " " + "WHERE" + " "
			+ DBConstants.ID + "=" + "?" + ";";

	public static final String TEMPLE_SELECT_ONE = "TEMPLE_SELECT_ONE";

	private static Map<String, PreparedStatement> preparedStmtMap = new HashMap<String, PreparedStatement>();

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
