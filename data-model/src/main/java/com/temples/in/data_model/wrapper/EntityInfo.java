package com.temples.in.data_model.wrapper;


public class EntityInfo {

	private Action action;
	private EntityType entityType;
	private PrimaryKey primaryKey;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entity) {
		this.entityType = entity;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

}
