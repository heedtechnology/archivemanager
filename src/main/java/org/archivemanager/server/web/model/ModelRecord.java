package org.archivemanager.server.web.model;
import java.util.ArrayList;
import java.util.List;

import org.heed.openapps.QName;
import org.heed.openapps.dictionary.ModelObject;


public class ModelRecord {
	private Long id;
	private String name;
	private String description;
	private String uid;
	private ModelRecord parent;	
	private String parentName;
	private String qname;
	private boolean entityIndexed;
	private boolean searchIndexed;
	private boolean auditable = true;
	private List<ModelObject> children = new ArrayList<ModelObject>();
	private List<ModelObject> fields = new ArrayList<ModelObject>();
	private List<ModelObject> sourceRelations = new ArrayList<ModelObject>();
	private List<ModelObject> targetRelations = new ArrayList<ModelObject>();
	private List<ModelObject> processors = new ArrayList<ModelObject>();
	
	
	public ModelRecord() {}
	public ModelRecord(org.heed.openapps.dictionary.Model m) {
		this.id = m.getId();
		this.qname = m.getQName().toString();
		this.name = m.getName();
	}	
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public ModelRecord getParent() {
		return parent;
	}
	public void setParent(ModelRecord parent) {
		this.parent = parent;
	}
	public String getName() {
		return name;
	}	
	public List<ModelObject> getChildren() {
		return children;
	}
	public void setChildren(List<ModelObject> children) {
		this.children = children;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEntityIndexed() {
		return entityIndexed;
	}
	public void setEntityIndexed(boolean entityIndexed) {
		this.entityIndexed = entityIndexed;
	}
	public boolean isSearchIndexed() {
		return searchIndexed;
	}
	public void setSearchIndexed(boolean searchIndexed) {
		this.searchIndexed = searchIndexed;
	}
	public boolean isAuditable() {
		return auditable;
	}
	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}
	
	public List<ModelObject> getFields() {
		return fields;
	}
	public void setFields(List<ModelObject> fields) {
		//Collections.sort(fields, new ModelFieldSorter());
		this.fields = fields;		
	}
	public List<ModelObject> getRelations() {
		List<ModelObject> list = new ArrayList<ModelObject>();
		list.addAll(sourceRelations);
		list.addAll(targetRelations);
		return list;
	}
	public ModelObject getField(QName qname) {
		for(ModelObject field : fields) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public ModelObject getSourceRelation(QName qname) {
		for(ModelObject field : sourceRelations) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public ModelObject getTargetRelation(QName qname) {
		for(ModelObject field : targetRelations) {
			if(field.getQName().equals(qname))
				return field;
		}
		return null;
	}
	public List<ModelObject> getFields(boolean inherited) {
		if(inherited) {
			List<ModelObject> fields = new ArrayList<ModelObject>();
			fields.addAll(this.getFields());
			return fields;
		} else return getFields();
	}
	public List<ModelObject> getSourceRelations() {
		return sourceRelations;
	}
	public List<ModelObject> getTargetRelations() {
		return targetRelations;
	}
	public List<ModelObject> getProcessors() {
		return processors;
	}
	public void setProcessors(List<ModelObject> processors) {
		this.processors = processors;
	}
	public String getQName() {
		return qname;
	}
	public void setQName(String qname) {
		this.qname = qname;
	}
}
