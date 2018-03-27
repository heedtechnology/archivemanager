package org.archivemanager.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.heed.openapps.QName;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.DefaultExportProcessor;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;


public class CollectionContentExportProcessor extends DefaultExportProcessor {
	private static final long serialVersionUID = 7176155657808313347L;
	
		
	@Override
	public Object export(FormatInstructions instructions, Entity entity) throws InvalidEntityException {
		if(instructions.getFormat().equals(FormatInstructions.FORMAT_CSV)) {
			return toCsv(entity);
		} else if(instructions.getFormat().equals(FormatInstructions.FORMAT_XML)) {
			String xml =  toXml(entity, true, true);
			return "<export>"+xml+"</export>";
		} else if(instructions.getFormat().equals(FormatInstructions.FORMAT_JSON)) {
			Map<String,Object> data = super.toMap(entity, instructions);			
			populatePathData(entity,data);			
			return data;
		} else {
			List<Entity> path = getComponentPath(entity);
			
			if(path.size() > 0) {
				getBuffer().append("<path>");
				long collectionId = 0;
				String collectionName = null;
				String collectionUrl = null;
				Long parentId = null;
				for(int i=0; i < path.size(); i++) {
					Entity node = path.get(i);
					String title = node.getName();
					String dateExpression = node.getPropertyValue(RepositoryModel.DATE_EXPRESSION);
					if(!node.getQName().equals(RepositoryModel.REPOSITORY)) {
						if(title != null  && !title.equals("")) getBuffer().append("<node id='"+node.getId()+"' type='"+node.getQName().getLocalName()+"' parent='"+parentId+"'><title><![CDATA["+title+"]]></title></node>");
						else if(dateExpression != null) getBuffer().append("<node id='"+node.getId()+"' type='"+node.getQName().getLocalName()+"' parent='"+parentId+"'><title>"+dateExpression+"</title></node>");
						parentId = node.getId();
					}
					if(node.getQName().equals(RepositoryModel.COLLECTION)) {
						collectionId = node.getId();
						collectionName = node.getName();
						collectionUrl = node.getPropertyValue(new QName("openapps.org_repository_1.0", "url"));
					}
				}
				getBuffer().append("</path>").toString();
				if(collectionId > 0) getBuffer().append("<collection_id>"+collectionId+"</collection_id>");
				if(collectionUrl != null) getBuffer().append("<collection_url>"+collectionUrl+"</collection_url>");
				if(collectionName != null) getBuffer().append("<collection_name><![CDATA["+collectionName+"]]></collection_name>");
			}		
			
			return toXml(entity, instructions.printSources(), instructions.printTargets());
		} 
	}
		
	public String toXml(Entity entity, boolean printSources, boolean printTargets) throws InvalidEntityException {
		StringBuffer buff = new StringBuffer("<node id='"+entity.getId()+"' uid='"+entity.getUid()+"' qname='"+entity.getQName().toString()+"' localName='"+entity.getQName().getLocalName()+"'>");
		buff.append("<name><![CDATA["+clean(entity.getName())+"]]></name>");
		for(Property property : entity.getProperties()) {
			if(property.getValue() != null && property.getValue().toString().length() > 0)
				buff.append("<property type='"+property.getType()+"' qname='"+property.getQName()+"'><![CDATA["+clean(String.valueOf(property.getValue()))+"]]></property>");
		}
		if(entity.getCreated() > 0) buff.append("<created>"+entity.getCreated()+"</created>");
		if(entity.getCreator() > 0) buff.append("<creator>"+entity.getCreator()+"</creator>");
		if(entity.getModified() > 0) buff.append("<modified>"+entity.getModified()+"</modified>");
		if(entity.getModifier() > 0) buff.append("<modifier>"+entity.getModifier()+"</modifier>");
		buff.append("<deleted>"+entity.getDeleted()+"</deleted>");
		buff.append("</node>");
		if(printSources) {
			List<Association> source_associations = entity.getSourceAssociations();
			for(Association association : source_associations) {
				Entity targetEntity = getEntityService().getEntity(association.getTarget());
				buff.append("<association qname='"+association.getQName()+"' source='"+association.getSource()+"' target='"+association.getTarget()+"' sourceUid='"+entity.getUid()+"' targetUid='"+targetEntity.getUid()+"'>");
				for(Property property : association.getProperties()) {
					if(property.getValue() != null && property.getValue().toString().length() > 0)
						buff.append("<property type='"+property.getType()+"' qname='"+property.getQName()+"'><![CDATA["+clean(String.valueOf(property.getValue()))+"]]></property>");
				}
				buff.append("</association>");					
				buff.append(toXml(targetEntity, true, false));	
				
			}
		}
		return buff.toString();
	}
	public String toCsv(Entity entity) throws InvalidEntityException {
		StringBuffer buff = new StringBuffer(entity.getId()+","+entity.getQName().getLocalName()+","+clean(entity.getName())+",");
		if(!entity.getQName().equals(RepositoryModel.COLLECTION) && !entity.getQName().equals(RepositoryModel.CATEGORY)) {
			for(Property property : entity.getProperties()) {
				if(property.getValue() != null && property.getValue().toString().length() > 0) {
					String value = csvClean(String.valueOf(property.getValue()));
					buff.append("\""+value+"\",");
				}
			}
			if(entity.getModified() > 0) buff.append(entity.getModified()+"\n");
		}
		List<Association> source_associations = entity.getSourceAssociations(RepositoryModel.ITEMS, RepositoryModel.CATEGORIES);
		for(Association association : source_associations) {
			Entity targetEntity = getEntityService().getEntity(association.getTarget());					
			buff.append(toCsv(targetEntity));			
		}		
		return buff.toString();
	}
	public Map<String,Object> exportMap(FormatInstructions instructions, Association association) {
		Map<String,Object> data = new HashMap<String,Object>();
		try {
			Entity target = instructions.printSources() ? association.getSourceEntity() : association.getTargetEntity();
			if(target == null) target = instructions.printSources() ? getEntityService().getEntity(association.getSource()) : getEntityService().getEntity(association.getTarget());
			if(instructions.isTree()) {
				data.put("localName", target.getQName().getLocalName());
				data.put("id", association.getTarget());
				data.put("parentLocalName", association.getSourceName().getLocalName());
				data.put("parent", association.getSource());
			} else {
				data.put("localName", target.getQName().getLocalName());
				data.put("id", association.getId());				
				data.put("source_id", association.getSource());
				data.put("target_id",association.getTarget());
			}
			data.put("uuid", target.getUid());
			data.put("name", target.getName());
			if(instructions.hasChildren()) data.put("isFolder", true);
			else data.put("isFolder", false);
			for(Property entityProperty : target.getProperties()) {
				if(entityProperty.getValue() != null && entityProperty.getValue().toString().length() > 0) {
					if(entityProperty.getQName().getLocalName().equals("function"))
						data.put("_function", entityProperty.getValue());
					else
						data.put(entityProperty.getQName().getLocalName(), entityProperty.getValue());
				}
			}
			for(Property assocProperty : association.getProperties()) {
				if(assocProperty.getQName().getLocalName().equals("function"))
					data.put("_function", assocProperty.getValue());
				else 
					data.put(assocProperty.getQName().getLocalName(), assocProperty.getValue());
			}
			populatePathData(target, data);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	protected void populatePathData(Entity entity, Map<String,Object> data) {
		List<Entity> path = getComponentPath(entity);			
		if(path.size() > 0) {
			List<Map<String,Object>> pathList = new ArrayList<Map<String,Object>>();
			long collectionId = 0;
			String collectionName = null;
			String collectionUrl = null;
			Long parentId = null;
			for(int i=0; i < path.size(); i++) {
				Entity node = path.get(i);
				String title = node.getName();
				String dateExpression = node.getPropertyValue(RepositoryModel.DATE_EXPRESSION);
				if(!node.getQName().equals(RepositoryModel.REPOSITORY)) {
					Map<String,Object> nodeMap = new HashMap<String,Object>();
					nodeMap.put("id", node.getId());
					nodeMap.put("type", node.getQName().getLocalName());
					nodeMap.put("parent", parentId);
					if(title != null  && !title.equals(""))
						nodeMap.put("title", title);
					else if(dateExpression != null)
						nodeMap.put("title", dateExpression);
					pathList.add(nodeMap);
					parentId = node.getId();
				}
				if(node.getQName().equals(RepositoryModel.COLLECTION)) {
					collectionId = node.getId();
					collectionName = node.getName();
					collectionUrl = node.getPropertyValue(new QName("openapps.org_repository_1.0", "url"));
				}
			}
			data.put("path", pathList);
			if(collectionId > 0) data.put("collection_id", collectionId);
			if(collectionUrl != null) data.put("collection_url", collectionUrl);
			if(collectionName != null) data.put("collection_name", collectionName);				
		}
	}
	protected List<Entity> getComponentPath(Entity comp) {
		List<Entity> path = new ArrayList<Entity>();
		if(comp != null) {
			Association parent = comp.getTargetAssociation(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			while(parent != null) {
				try {
					Entity p = getEntityService().getEntity(parent.getSource());
					path.add(p);
					parent = p.getTargetAssociation(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.reverse(path);
		return path;
	}
	protected String csvClean(Object in) {
		if(in == null) return "";
		return in.toString().replaceAll("\\<.*?\\>", "").replace("\n", "");
	}
}
