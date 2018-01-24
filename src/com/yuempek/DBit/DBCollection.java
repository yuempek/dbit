package com.yuempek.DBit;

import java.util.LinkedHashMap;

import android.content.Context;


@SuppressWarnings("serial")
public class DBCollection extends LinkedHashMap<String, Database> {

	Context context;
	
	public DBCollection(Context context) {
		super();
		this.context = context;
	}	
	
	public Database AddDatabase(String name){
		Database db = new Database(name, this, this.context);
		this.put(name, db);
		return db;
	}
	
}
