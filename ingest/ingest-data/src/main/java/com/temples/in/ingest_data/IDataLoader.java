package com.temples.in.ingest_data;

import com.temples.in.data_model.Temple;

public interface IDataLoader {

	public abstract Temple addTemple(Temple temple);

}