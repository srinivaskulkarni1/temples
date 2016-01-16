package com.temples.in.ingest_data.data_loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.datastax.driver.core.Session;
import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.EntityGroup;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_data.IParamsBulder;
import com.temples.in.ingest_data.QueryStrings;
import com.temples.in.ingest_data.data_access.IDBConnection;
import com.temples.in.ingest_data.data_access.IInsertStatementExecutor;
import com.temples.in.ingest_util.BeanConstants;

public class TempleDataLoader implements ApplicationContextAware, IDataLoader {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(TempleDataLoader.class);

	@Override
	public BaseEntity add(BaseEntity entity) {
		Temple temple = (Temple) entity;
		LOGGER.debug("Processing | Entity Group={}, Entity Id={} | {}.add",
				EntityGroup.TEMPLES, temple.getId(), TempleDataLoader.class.getSimpleName());

		IDBConnection dbConnection = getDBConnection();
		IInsertStatementExecutor insertStatementExecutor = getInsertStatementExecutor();
		IParamsBulder paramsBuilder = getParamsBuilder();

		Session session = dbConnection.getSession();
		
		LOGGER.info("Creating new entity | Entity Group={}, Entity Id={}",
				EntityGroup.TEMPLES, temple.getId());		

		boolean bInserted = insertStatementExecutor.executeInsert(temple.getId(),
				session, QueryStrings.TEMPLE_INSERT_QUERY,
				paramsBuilder.buildParams(temple));

		LOGGER.info("New entity created successfully | Entity Group={}, Entity Id={}",
				EntityGroup.TEMPLES, temple.getId());
		
		LOGGER.debug("Processed | Entity Group={}, Entity Id={} | {}.add",
				EntityGroup.TEMPLES, temple.getId(), TempleDataLoader.class.getSimpleName());

		if (!bInserted) {
			return null;
		}

		return temple;
	}

	private IDBConnection getDBConnection() {
		return (IDBConnection) context.getBean(BeanConstants.DB_CONNECTION);
	}

	private IParamsBulder getParamsBuilder() {
		return (IParamsBulder) context.getBean(BeanConstants.PARAMS_BUILDER);
	}

	private IInsertStatementExecutor getInsertStatementExecutor() {
		return (IInsertStatementExecutor) context
				.getBean(BeanConstants.INSERT_STMT_EXECUTOR);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = (AbstractApplicationContext) applicationContext;
	}
}
