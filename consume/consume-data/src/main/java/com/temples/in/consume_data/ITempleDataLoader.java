package com.temples.in.consume_data;

import com.temples.in.data_model.Temple;
import com.temples.in.data_model.wrapper.PrimaryKey;

public interface ITempleDataLoader {

/*	public abstract List<Temple> getTempleList();
*/
	public abstract Temple getTemple(int consumerId, PrimaryKey primaryKey);

}