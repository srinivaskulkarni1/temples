package com.temples.in.ingest_data.data_access;

import com.datastax.driver.core.Session;

public interface IDBConnection {

	public abstract Session getSession();

	public abstract void connect();
}