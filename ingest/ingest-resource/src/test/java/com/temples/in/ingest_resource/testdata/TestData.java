package com.temples.in.ingest_resource.testdata;

import java.util.ArrayList;
import java.util.List;

import com.temples.in.data_model.Temple;

public class TestData {

	public static List<Temple> getDummyTempleList(){
		List<Temple> templeList = new ArrayList<Temple>();
		Temple t = new Temple();
		t.setDistrict("Udupi");
		t.setGod("Shri Krishna");
		t.setPlace("Udupi");
		t.setState("Karnataka");
		templeList.add(t);
		return templeList;
	}
}
