package com.temples.in.query_data.data_access;

import com.datastax.driver.core.Session;

public interface IDBConnection {

	public abstract Session getSession();
	
	public abstract void connect();
	
}