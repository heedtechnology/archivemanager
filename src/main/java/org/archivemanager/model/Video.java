package org.archivemanager.model;


public class Video extends Item {
	private String rendition;
	private String avatar;
	
	
	public Video(long id, String title, String description, String contentType) {
		super(id, title, description, contentType);
	}
	
	public String getRendition() {
		return rendition;
	}
	public void setRendition(String rendition) {
		this.rendition = rendition;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
		
}
