package com.temples.in.query_data;

import java.util.List;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.data_model.wrapper.PrimaryKey;

public interface IDataLoader {

	public abstract List<BaseEntity> getAll();
	
	public abstract BaseEntity getOne(PrimaryKey primaryKey);

}