package com.temples.in.ingest_data;

import com.temples.in.data_model.table_info.DBConstants;

public class QueryStrings {

	public static final String TEMPLE_INSERT_QUERY = "INSERT INTO "
			+ DBConstants.TABLE_TEMPLE + "(" + DBConstants.ID + ","
			+ DBConstants.ENTITY + ")" + "VALUES (?,?);";
	
	public static final String TRANS_INFO_INSERT_QUERY = "INSERT INTO "
			+ DBConstants.TABLE_TRANSACTION_INFO + "(" + DBConstants.TRANSACTION_ID + ","
			+ DBConstants.ID + ","
			+ DBConstants.TRANSACTION_DATE + ","
			+ DBConstants.DELETED + ")" + "VALUES (?,?,?,?);";

}
