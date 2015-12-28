package com.temples.in.ingest_data;

import com.temples.in.data_model.Temple;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Object[] objList = { "String", 12.5, 11, new Temple() };

		for (int i = 0; i < objList.length; i++) {
			if (objList[i].getClass().equals(String.class)) {
				System.out.println(objList[i].getClass() + " / "
						+ objList[i].getClass().getSimpleName());
			}
			System.out.println(objList[i].getClass().getSimpleName());
		}
	}

}
