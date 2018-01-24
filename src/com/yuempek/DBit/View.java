package com.yuempek.DBit;

@SuppressWarnings("serial")
public class View extends Table{

	protected String query = "";

	public View(String name, String query, Database parent) {
		super(name, parent);
		this.readOnly = true;
		this.query = query;
	}
	
	@Override
	public String getStructureSQL() {
		return "CREATE VIEW IF NOT EXISTS " + this.name + " AS " + this.query; 
	}
	
	@Override
	public String getDropSQL() {
		return "DROP VIEW " + this.name; 
	}
	

}
