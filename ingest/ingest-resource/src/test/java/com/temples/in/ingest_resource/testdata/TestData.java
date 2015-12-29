package com.temples.in.ingest_resource.testdata;

import java.net.URI;
import java.net.URISyntaxException;
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
	
	public static Temple getNewTemple(){
		Temple t = new Temple();
		t.setDistrict("Udupi");
		t.setGod("Shri Krishna");
		t.setPlace("Udupi");
		t.setState("Karnataka");
		return t;
	}
	
	public static URI getURI() {
		try {
			return new URI("test");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
