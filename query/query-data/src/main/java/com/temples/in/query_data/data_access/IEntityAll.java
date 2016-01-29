package com.temples.in.query_data.data_access;

import java.util.List;

import com.temples.in.data_model.BaseEntity;

public interface IEntityAll {
	public <T extends BaseEntity> List<T> getAll();
}
