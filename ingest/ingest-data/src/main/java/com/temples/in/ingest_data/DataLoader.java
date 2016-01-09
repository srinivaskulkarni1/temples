package com.temples.in.ingest_data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.datastax.driver.core.Session;
import com.temples.in.data_model.Temple;
import com.temples.in.ingest_data.data_access.IDBConnection;
import com.temples.in.ingest_data.data_access.IInsertStatementExecutor;
import com.temples.in.ingest_util.BeanConstants;

public class DataLoader implements ApplicationContextAware, IDataLoader {

	private AbstractApplicationContext context;

	private static Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);

	@Override
	public Temple addTemple(Temple temple) {
		LOGGER.debug("Processing | Id={} | {}.addTemple",
				temple.getId(), DataLoader.class.getSimpleName());

		IDBConnection dbConnection = getDBConnection();
		IInsertStatementExecutor insertStatementExecutor = getInsertStatementExecutor();
		IParamsBulder paramsBuilder = getParamsBuilder();

		Session session = dbConnection.getSession();
		
		LOGGER.info("Creating new entity | Id={}",
				temple.getId());		

		boolean bInserted = insertStatementExecutor.executeInsert(temple.getId(),
				session, QueryStrings.TEMPLE_INSERT_QUERY,
				paramsBuilder.buildTempleParams(temple));

		LOGGER.info("New entity created successfully | Id={}",
				temple.getId());
		
		LOGGER.debug("Processed | Id={} | {}.addTemple",
				temple.getId(), DataLoader.class.getSimpleName());

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
