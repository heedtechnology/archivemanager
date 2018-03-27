package org.archivemanager.model;
import java.util.ArrayList;
import java.util.List;

import org.heed.openapps.dictionary.ContactModel;
import org.heed.openapps.SystemModel;
import org.heed.openapps.entity.Entity;

public class Individual {
	private long id;
	private String title;
	private String biography;
	private List<DigitalObject> attachments = new ArrayList<DigitalObject>();

	
	public Individual(Entity entity) {
		this.id = entity.getId();
		this.title = entity.getPropertyValue(SystemModel.NAME);
		this.biography = entity.getPropertyValue(ContactModel.BIOGRAPHY);
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	
	public List<DigitalObject> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<DigitalObject> attachments) {
		this.attachments = attachments;
	}
	
}
