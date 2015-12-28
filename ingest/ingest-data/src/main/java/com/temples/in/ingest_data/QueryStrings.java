package com.temples.in.ingest_data;

public class QueryStrings {

	public static final String TEMPLE_INSERT_QUERY = "INSERT INTO "
			+ DBConstants.TABLE_TEMPLE + "(" + DBConstants.TABLE_TEMPLE_GOD
			+ "," + DBConstants.TABLE_TEMPLE_PLACE + ","
			+ DBConstants.TABLE_TEMPLE_DISTRICT + ","
			+ DBConstants.TABLE_TEMPLE_STATE + ")" + "VALUES (?,?,?,?);";
}
