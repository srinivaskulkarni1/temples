package com.temples.in.ingest_data;

import com.temples.in.data_model.table_info.DBConstants;

public class QueryStrings {

	public static final String TEMPLE_INSERT_QUERY = "INSERT INTO "
			+ DBConstants.TABLE_TEMPLE + "(" + DBConstants.ID + ","
			+ DBConstants.TABLE_TEMPLE_GOD + ","
			+ DBConstants.TABLE_TEMPLE_PLACE + ","
			+ DBConstants.TABLE_TEMPLE_DISTRICT + ","
			+ DBConstants.TABLE_TEMPLE_STATE + ")" + "VALUES (?,?,?,?,?);";
}
