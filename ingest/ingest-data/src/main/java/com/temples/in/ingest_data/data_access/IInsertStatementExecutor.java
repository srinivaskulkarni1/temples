package com.temples.in.ingest_data.data_access;

import java.util.List;

import com.datastax.driver.core.Session;
import com.temples.in.ingest_data.Params;

public interface IInsertStatementExecutor {

	public abstract boolean executeInsert(Session session, String queryString,
			List<Params> params);

}