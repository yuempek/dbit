package com.yuempek.DBit;

import java.sql.Date;


public class Column<T>{
	
	public Table parentTable;
	public String columnName;
	public ColumnType columnType;
	public int maxLength;
	public T value = null;
	
	//static booleans
	private boolean flag_PK = false;
	private boolean flag_AutoIncremental = false;

	protected boolean readOnly = false;

	
	//variable booleans
	private boolean flag_ValueIsNull= false;
	private boolean flag_UseInWhere= false;
	
	
	public Column(String name, int maxLength, Table parent) {
		this.columnName = name;
		this.maxLength = maxLength;
		this.parentTable = parent;
	}	

	public String getStructureSQL() {
		String sql = "";
		String type;
		
		switch (this.columnType) {
			case INTEGER: type = "INTEGER"; break;
			case TEXT:    type = "TEXT";    break;
			case REAL:    type = "REAL";    break;
			case BLOB:    type = "BLOB";    break;
			case BOOLEAN: type = "REAL";    break;
			case DATE:    type = "REAL";    break;
			case LONG:    type = "REAL";    break;
			case STRING:  type = "TEXT";    break;
			default:      type = "TEXT";    break;
		}

		sql += this.columnName + " " + type + " " + (this.isPK()?"PRIMARY KEY":"") + " " + (this.isAutoIncremental()?"AUTOINCREMENT":"");
		
		return sql;
	}

	public Column<T> useAsPK() {
		flag_PK = true;
		return this;
	}
	
	public boolean isPK() {
		return flag_PK;
	}
	
	public Column<T> notUseAsPK() {
		flag_PK = false;
		return this;
	}
	
	public Column<T> useAsAutoIncremental() {
		flag_AutoIncremental = true;
		return this;
	}
	
	public boolean isAutoIncremental() {
		return flag_AutoIncremental;
	}
	
	public Column<T> notUseAsAutoIncremental() {
		flag_AutoIncremental = false;
		return this;
	}
	
	public Column<T> useInWhere(){
		flag_UseInWhere = true;
		return this;
	}
	
	public boolean isUsingInWhere(){
		return flag_UseInWhere;
	}
	
	public Column<T> notUseInWhere(){
		flag_UseInWhere = false;
		return this;
	}
	
	public Column<T> setValueAsNull(){
		flag_ValueIsNull = true;
		this.value = null;
		return this;
	}
	
	public boolean isValueNull(){
		return flag_ValueIsNull;
	}
	
	public Column<T> clearValueAsNull(){
		flag_ValueIsNull = false;
		return this;
	}
	
	public Column<T> clear() {
		this.value = null;
		this.notUseInWhere();
		this.clearValueAsNull();
		return this;
	}
	
	public Boolean hasValue() {
		return isValueNull() || (value != null);
	}
	
	@SuppressWarnings("unchecked")
	public void setValueFromString(String strValue){
		
		switch (this.columnType) {
			case STRING:
				value = (T)strValue;
				break;
			case DATE:
				value = (T)Date.valueOf(strValue);
				break;
			case INTEGER:
				value = (T)(Integer)Integer.valueOf(strValue);
				break;
			case LONG:
				value = (T)(Long)Long.valueOf(strValue);
				break;
			case BOOLEAN:
				value = (T)(Boolean)Boolean.valueOf(strValue);
				break;
			default:
				return;
		}
		
	}
	
	@Override
	public String toString() {
		if (isValueNull()) return "NULL";

		String str;
		switch (this.columnType) {
			case STRING:
				str = "'"+(((String)value).replaceAll("'", "''"))+"'";
				break;
			case DATE:
				str = "'"+((Date)value).toString()+"'";
				break;
			case INTEGER:
				str = ""+value.toString()+"";
				break;
			case LONG:
				str = ""+value.toString()+"";
				break;
			case BOOLEAN:
				str = ""+((Boolean)value?"true":"false")+"'";
				break;
			default:
				str = "''";
		}
		
		return str;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Column clone() {
		Column<T> c = new Column<T>(this.columnName, this.maxLength, this.parentTable);
		c.columnType = this.columnType;
		c.flag_AutoIncremental = this.flag_AutoIncremental;
		c.flag_PK = this.flag_PK;
		c.flag_UseInWhere = this.flag_UseInWhere;
		c.flag_ValueIsNull = this.flag_ValueIsNull;
		c.value = this.value;
		c.readOnly = this.readOnly;
		return c;
	}
	
}