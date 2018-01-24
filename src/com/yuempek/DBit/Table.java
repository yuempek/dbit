package com.yuempek.DBit;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

import android.database.Cursor;

import com.yuempek.DBit.ColumnType;

@SuppressWarnings({ "serial", "rawtypes" })
public class Table extends LinkedHashMap<String, Column>{

	public String name;
	
	public Database parentDatabase;

	protected boolean readOnly = false;

	public Table(String name, Database parent) {
		super();
		this.name = name;
		this.parentDatabase = parent;
	}

	public Column column(String columnName){
		return this.get(columnName);		
	}
	
	@SuppressWarnings({ "unchecked" })
	public Column AddColumn(String columnName, ColumnType columnType, int maxLength){
		Column column;
		
		switch (columnType) {
			case STRING:
				column = new Column<String>(columnName, maxLength, this);
				column.columnType = columnType;
				break;
			case DATE:
				column= new Column<Date>(columnName, maxLength, this);
				column.columnType = columnType;
				break;
			case INTEGER:
				column= new Column<Integer>(columnName, maxLength, this);
				column.columnType = columnType;
				break;
			case LONG:
				column= new Column<Long>(columnName, maxLength, this);
				column.columnType = columnType;
				break;
			case BOOLEAN:
				column= new Column<Boolean>(columnName, maxLength, this);
				column.columnType = columnType;
				break;
			default:
				return null;
		}
		column.readOnly = this.readOnly;
		this.put(columnName, column);
		return column;
	}

	@SuppressWarnings({ "unchecked" })
	public Column AddColumn(Column column){
		
		if(column != null){
			column.readOnly = this.readOnly;
			column.parentTable = this;
			this.put(column.columnName, column);
		}
		return column;
	}

	public String getStructureSQL() {
		
		if(this.readOnly) return null;
		
		String sql = "CREATE TABLE " + this.name + " (" + "\n";
		
		Iterator<Map.Entry<String, Column>> i = this.entrySet().iterator();

		while(i.hasNext()) sql += i.next().getValue().getStructureSQL() + (i.hasNext()?",":"") + "\n";

		sql += " );" + "\n";

		return sql;
	}

	public String getDropSQL() {

		if(this.readOnly) return null;
		
		return "DROP TABLE " + this.name + ";\n";
	}

	public Row newRow() {
		
		Row r = new Row(this);
		
		r.readOnly = this.readOnly;

		Iterator<Map.Entry<String, Column>> i = this.entrySet().iterator();

		while(i.hasNext()){
			Entry<String, Column> entry = i.next();
			
			String name = entry.getKey();

			Column column = entry.getValue().clone().clear();
			
			r.put(name, column);
		}

		return r;
	}
	
	public Row[] getData() {
		return getData("1=1");
	}
		
	public Row[] getData(String whereConditions) {
		
		ArrayList<Column> columns = new ArrayList<Column>();
		Iterator<Map.Entry<String, Column>> i = this.entrySet().iterator();

		while(i.hasNext()){
			columns.add(i.next().getValue());
		}
		
		return getData(whereConditions, columns.toArray(new Column[columns.size()]));
	}
	
	public Row[] getData(String whereConditions, Column[] columns) {
		ArrayList<Row> records = new ArrayList<Row>();
		String columnstr = "";

		for(int i = 0; i < columns.length; i++){
			columnstr += columns[i].columnName + ( i + 1 == columns.length ? "" : ", ");
		}
		
		Cursor cur =  this.parentDatabase.db.rawQuery( "select " + columnstr + " from " + this.name + " where " + whereConditions, null );
	    cur.moveToFirst();
	    
	    while(!cur.isAfterLast()){
	    	
	    	Row r = this.newRow();
	    	String cn;
	    	Column c;
	    	for(int i = 0; i < columns.length; i++){
	    		cn = columns[i].columnName;
	    		try {
					c = r.Column(cn);
					//c.setValue(cur.getString(cur.getColumnIndex(c.columnName)));
					c.setValueFromString(cur.getString(i));
				} catch (Exception e) {
					e.printStackTrace();
				}	    		 
			}
	    	
	    	records.add(r);
	    	cur.moveToNext();
	    }		
		
		return records.toArray(new Row[records.size()]);
	}

}
