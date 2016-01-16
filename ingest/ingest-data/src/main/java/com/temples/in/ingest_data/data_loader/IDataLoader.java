package com.temples.in.ingest_data.data_loader;

import com.temples.in.data_model.BaseEntity;


public interface IDataLoader {

	public abstract BaseEntity add(BaseEntity entity);

}