package com.temples.in.ingest_data.data_access;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;

public interface IDBConnection {

	public abstract Session getSession();

	public abstract MappingManager getManager();

	public abstract void connect();

	// public abstract ResultSet getResultSet(String tableName);
}