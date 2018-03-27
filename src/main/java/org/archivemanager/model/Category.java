package org.archivemanager.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;


public class Category extends Result {
	private List<Category> categories = new ArrayList<Category>();
	private List<Item> items = new ArrayList<Item>();
	
	
	public Category() {}
	public Category(long id, String name) {
		super(id, name);
	}
	public Category(Element entity) {
		super(Long.valueOf(entity.attribute("id").getText()), entity.element("name").getText());		
	}
	
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}	
}
