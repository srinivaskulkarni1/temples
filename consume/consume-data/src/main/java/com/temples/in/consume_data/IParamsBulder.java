package com.temples.in.consume_data;

import java.util.List;

import com.temples.in.data_model.wrapper.PrimaryKey;

public interface IParamsBulder {

	public abstract List<Params> build(PrimaryKey primaryKey);

}