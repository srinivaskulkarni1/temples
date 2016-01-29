package com.temples.in.query_data.data_access;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.temples.in.common_utils.ApplicationConfiguration;
import com.temples.in.data_model.table_info.DBConstants;

@Component
public class PreparedStatements {

	private static Logger LOGGER = LoggerFactory
			.getLogger(PreparedStatements.class);

	@Autowired
	private ApplicationConfiguration configuration;
	
	@Autowired
	@Qualifier("cassandrastore")
	private IDBConnection connection;
	
	private PreparedStatement templesById;
	private PreparedStatement allTemples;
	
	public PreparedStatement getTemplesById() {
		return templesById;
	}

	public PreparedStatement getAllTemples() {
		return allTemples;
	}

	
	@PostConstruct
	public void init() {
		
		LOGGER.debug("Creating prepared statements...");
		
		templesById = connection.getSession().prepare(
				QueryBuilder
						.select()
						.from(configuration.getCassandraKeyspace(),
								DBConstants.TABLE_TEMPLE)
						.where(QueryBuilder.eq(DBConstants.ID,
								QueryBuilder.bindMarker())).limit(1));

		allTemples = connection.getSession().prepare(
				QueryBuilder.select().from(
						configuration.getCassandraKeyspace(),
						DBConstants.TABLE_TEMPLE));

	}
}