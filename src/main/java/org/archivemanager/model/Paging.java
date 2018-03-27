package org.archivemanager.model;

public class Paging {
	private String name;
	private String query;
	
	
	public Paging(String name, String query) {
		this.name = name;
		this.query = query;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
}
