package com.yuempek.DBit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressWarnings("serial")
public class Database extends LinkedHashMap<String, Table> {
	
	private DatabaseHelper dbHelper;
	private Context context;
	public int databaseVersion = 3;
	public String databaseName;
	public SQLiteDatabase db;
	public SQLiteDatabase dbReadOnly;
	public DBCollection parentDBCollection;
	
	
	public Database(String name, DBCollection parentDBCollection, Context context) {
		super();
		this.databaseName = name;
		this.parentDBCollection = parentDBCollection;
		this.context = context;
	}
	
	public Table table(String tablename){
		return this.get(tablename);		
	}
	
	public Table AddTable(String tablename){
		Table table = new Table(tablename, this);
		this.put(tablename, table);
		return table;
	}
	
	public View AddView(String viewname, String sql){
		View view = new View(viewname, sql, this);
		this.put(viewname, (Table) view);
		return view;
	}

	public Table AddTable(Table table){
		this.put(table.name, table);		
		return table;
	}
	
	public void open(){
		this.dbHelper = new DatabaseHelper(this.context);
		this.db = this.dbHelper.getWritableDatabase();		
	}
	
	public List<String> getStructureSQLs(){
		List<String> sql_tables = new ArrayList<String>();
		List<String> sql_views  = new ArrayList<String>();
		
		Iterator<Map.Entry<String, Table>> i = this.entrySet().iterator();

		while(i.hasNext()){
			Table t = i.next().getValue();
			if(t.readOnly) 
				sql_views.add(t.getStructureSQL());
			else 
				sql_tables.add(t.getStructureSQL());
			 
		}

		sql_tables.addAll(sql_views);
		return sql_tables;
	}
	
	public List<String> getAllTablesDropSQLs(){
		List<String> sql = new ArrayList<String>();
		
		Iterator<Map.Entry<String, Table>> i = this.entrySet().iterator();

		while(i.hasNext()) sql.add(i.next().getValue().getDropSQL());

		return sql;
	}
	
	public void reCreateDatabase(){		
		List<String> sqls = getAllTablesDropSQLs();
		Iterator<String> i = sqls.iterator();
		
		while (i.hasNext()) {
			try {
				this.db.execSQL(i.next());
			} catch (Exception e) {
				Log.e("yuempek.database", e.getMessage());
			}			
		}
		createDatabase();
	}
	
	private void createDatabase(){
		List<String> sqls = getStructureSQLs();
		Iterator<String> i = sqls.iterator();
		
		while (i.hasNext()) {
			try {
				this.db.execSQL(i.next());
			} catch (Exception e) {
				Log.e("yuempek.database", e.getMessage());
			}			
		}	
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, databaseName, null, databaseVersion);
		}
	
		@Override
		public void onCreate(SQLiteDatabase _db) {
			db = _db;
			createDatabase();			
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			db = _db;
			reCreateDatabase();
		}
		
	}
}

