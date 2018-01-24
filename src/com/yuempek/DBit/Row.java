package com.yuempek.DBit;

import java.util.LinkedHashMap;
import java.util.Iterator;

@SuppressWarnings({ "serial", "rawtypes" })
public class Row extends LinkedHashMap<String, Column>{

	protected boolean readOnly = false;

	public Table parentTable;
	
	public Row(Table parent) {
		super();
		this.parentTable = parent;
	}
	
	public Column Column(String columnName) throws Exception {
		Column column = this.get(columnName);

		if(column == null) throw new Exception("The column \""+columnName+"\" is not exists in this table!");

		return column;
	}
	
	 public void update() throws Exception{
		if(this.parentTable != null && this.parentTable.readOnly) return;
		
		String sql = "UPDATE " + this.parentTable.name + " SET " + "\n";
		String where = " WHERE 1=1 ";

		Boolean isValueExits = false;
		
		Iterator<Entry<String, Column>> i = this.entrySet().iterator();
		
		while(i.hasNext()){
			
			Entry<String, Column> e = i.next();
			
			Column column  = e.getValue();
			
			if(column.hasValue()){
				isValueExits = true;

				sql += column.columnName + (!column.isValueNull()?" = ":" IS ") + column.toString() + ", ";
			}
			
			if(column.isUsingInWhere()){
				where += " AND ";
				where += column.columnName + (!column.isValueNull()?" = ":" IS ") + column.toString();
				where += "\n";
			}
		}
		
		if(!isValueExits) throw new Exception("Any value is not exists to update!");
		
		sql = sql.replaceFirst(",\\s*$", "");
		sql += where;
		sql += " ;" + "\n";
		
		this.parentTable.parentDatabase.db.execSQL(sql);
	 }
	 
	 public void insert(){
		 
		if(this.parentTable != null && this.parentTable.readOnly) return;
			
		String sql = "INSERT INTO " + this.parentTable.name + "([[COLUMNS]]) VALUES ([[VALUES]])" + "\n";
		String columns = "";
		String values = "";
		Iterator<Entry<String, Column>> i = this.entrySet().iterator();
		
		while(i.hasNext()){
			
			Entry<String, Column> e = i.next();
			
			Column column  = e.getValue();
			
			if(column.hasValue()){
				columns += column.columnName + ", ";
				values  += column.toString() + ", ";
			}			
		}
		
		// if(!isValueExits) throw new Exception("Any value is not exists to insert!");

		columns = columns.replaceFirst(",\\s*$", "");
		values  =  values.replaceFirst(",\\s*$", "");
		
		sql = sql.replace("[[COLUMNS]]", columns);
		sql = sql.replace("[[VALUES]]", values);
		sql += " ;" + "\n";
			
		this.parentTable.parentDatabase.db.execSQL(sql);
	 }
	 
	public void delete(){
		
		if(this.parentTable != null && this.parentTable.readOnly) return;
		
		String sql = "DELETE FROM " + this.parentTable.name + " \n";
		String where = " WHERE 1=1 ";
		
		Iterator<Entry<String, Column>> i = this.entrySet().iterator();
		
		while(i.hasNext()){
			
			Entry<String, Column> e = i.next();
			
			Column column  = e.getValue();
						
			if(column.isUsingInWhere()){
				where += " AND ";
				where += column.columnName + (!column.isValueNull()?" = ":" IS ") + column.toString();
				where += "\n";
			}
		}
		
		sql += where;
		sql += " ;" + "\n";
			
		this.parentTable.parentDatabase.db.execSQL(sql);
	}
	 
	public Row[] select() {
		String where = " 1=1 ";
		
		Iterator<Entry<String, Column>> i = this.entrySet().iterator();
		
		while(i.hasNext()){
			
			Entry<String, Column> e = i.next();
			
			Column column  = e.getValue();
						
			if(column.isUsingInWhere()){
				where += " AND ";
				where += column.columnName + (!column.isValueNull()?" = ":" IS ") + column.toString();
				where += "\n";
			}
		}
		
		return this.parentTable.getData(where);
	}


}
