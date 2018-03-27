package org.archivemanager.model;


public class Result {
	private long id;
	private long user;
	private String username;
	private String name = "";
	private String description = "";
	private String contentType = "";
		
	
	public Result() {}
	public Result(long id, String name) {
		this.id = id;
		this.name = name;
	}
	public Result(long id, String name, String description, String contentType) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.contentType = contentType;
	}
			
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}	
	public long getUser() {
		return user;
	}
	public void setUser(long user) {
		this.user = user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
		
}
