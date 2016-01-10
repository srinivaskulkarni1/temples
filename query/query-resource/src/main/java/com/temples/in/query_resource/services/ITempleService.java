package com.temples.in.query_resource.services;

import java.util.List;

import com.temples.in.data_model.Temple;

public interface ITempleService {

	public abstract List<Temple> getTemples();
	
	public abstract Temple getTemple(String id);

}