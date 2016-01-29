package com.temples.in.query_data.data_access;

import java.util.List;

import com.temples.in.data_model.BaseEntity;
import com.temples.in.query_data.Params;

public interface IEntityById {
	public <T extends BaseEntity> List<T> getById(List<Params> params);
}
