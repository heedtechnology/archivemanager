package org.archivemanager.model;
import java.util.ArrayList;
import java.util.List;


public class Subject extends Result {
	private String type;
	private String source;
	private List<Collection> collections = new ArrayList<Collection>();
	
	
	public Subject(long id, String title, String description, String contentType) {
		super(id, title, description, contentType);
	}	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<Collection> getCollections() {
		return collections;
	}
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}
	
}
