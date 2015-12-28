package com.temples.in.ingest_data;

import java.util.List;

import com.temples.in.data_model.Temple;

public interface IParamsBulder {

	public abstract List<Params> buildTempleParams(Temple temple);

}