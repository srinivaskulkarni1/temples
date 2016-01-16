package com.temples.in.data_model;

import com.google.common.base.Objects;

public class Temple extends BaseEntity {
	
	private String god;
	private String place;
	private String state;
	private String district;

	public Temple() {
		super();
	}
	
	
	
	public String getGod() {
		return god;
	}

	public void setGod(String god) {
		this.god = god;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
	
	public boolean equals(Object obj) {
        if (obj instanceof Temple) {
        	Temple that = (Temple) obj;
            return Objects.equal(this.getId(), that.getId());
        }
        return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
