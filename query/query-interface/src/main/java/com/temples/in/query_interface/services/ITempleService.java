package com.temples.in.query_interface.services;

import java.util.List;
import java.util.Map;

import com.temples.in.data_model.Temple;

public interface ITempleService {

	public abstract List<Temple> getTemples();
	
	public abstract Temple getTemple(String id);

	public abstract List<Temple> getFilteredTemples(Map<String, String[]> parameterMap);

}