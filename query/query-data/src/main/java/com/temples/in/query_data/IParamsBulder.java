package com.temples.in.query_data;

import java.util.List;

import com.temples.in.data_model.wrapper.PrimaryKey;

public interface IParamsBulder {

	public abstract List<Params> build(String entityId, PrimaryKey primaryKey);

}