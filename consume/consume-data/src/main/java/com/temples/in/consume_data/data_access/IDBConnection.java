package com.temples.in.consume_data.data_access;

import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.temples.in.consume_data.Params;

public interface IDBConnection {

	public abstract Session getSession();
	
	public abstract MappingManager getManager();

	public abstract void connect();
	
	public abstract ResultSet getAll(String tableName);
	
	public abstract ResultSet getOne(String statementId, String queryString, List<Params> params);
}