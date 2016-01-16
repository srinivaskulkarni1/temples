package com.temples.in.query_data.data_access;

import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.temples.in.query_data.Params;

public interface IDBConnection {

	public abstract Session getSession();
	
	public abstract void connect();
	
	public abstract ResultSet getAll(String statementId);
	
	public abstract ResultSet getOne(String statementId, List<Params> params);
}