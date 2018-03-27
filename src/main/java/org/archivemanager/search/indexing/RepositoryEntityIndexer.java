package org.archivemanager.search.indexing;

import java.util.logging.Logger;

import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.QName;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.SystemModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.entity.indexing.IndexField;


public class RepositoryEntityIndexer extends DefaultEntityIndexer {
	private final static Logger log = Logger.getLogger(RepositoryEntityIndexer.class.getName());
	
	
	@Override
	public void index(Entity entity, IndexEntity data) throws InvalidEntityException {
		super.index(entity, data);
		/*				
		Property summary = entity.getProperty(RepositoryModel.SUMMARY);
		if(summary != null) {
			String value = (String)summary.getValue();
			if(value != null && value.length() > 0 && !value.equals("<br>") && !value.startsWith("<!--[if gte mso 9]>"))
				data.appendFreeText(value);
		}
		if(entity.getQName().equals(RepositoryModel.COLLECTION)) {
			Property scope = entity.getProperty(RepositoryModel.SCOPE_NOTE);
			if(scope != null) {
				String value = (String)scope.getValue();
				if(value != null && value.length() > 0 && !value.equals("<br>") && !value.startsWith("<!--[if gte mso 9]>"))
					data.appendFreeText(value);
			}
			Property bio = entity.getProperty(RepositoryModel.BIOGRAPHICAL_NOTE);
			if(bio != null) {
				String value = (String)bio.getValue();
				if(value != null && value.length() > 0 && !value.equals("<br>") && !value.startsWith("<!--[if gte mso 9]>"))
					data.appendFreeText(value);
			}
		}
		*/
		if(entity.getQName().equals(RepositoryModel.COLLECTION))
			data.getFields().add(new IndexField(Property.INTEGER, "sort_", 4L, false));
		else if(entity.getQName().equals(ClassificationModel.PERSON) || entity.getQName().equals(ClassificationModel.CORPORATION) || entity.getQName().equals(ClassificationModel.FAMILY))
			data.getFields().add(new IndexField(Property.INTEGER, "sort_", 3L, false));
		else if(entity.getQName().equals(ClassificationModel.SUBJECT))
			data.getFields().add(new IndexField(Property.INTEGER, "sort_", 2L, false));
		else
			data.getFields().add(new IndexField(Property.INTEGER, "sort_", 0L, false));
		Association parent_assoc = entity.getTargetAssociation(RepositoryModel.CATEGORIES);
		if(parent_assoc == null) parent_assoc = entity.getTargetAssociation(RepositoryModel.ITEMS);
		if(parent_assoc != null && parent_assoc.getSource() != null) {
			Entity parent = getEntityService().getEntity(parent_assoc.getSource());
			data.getFields().add(new IndexField(Property.LONG, "parent_id", parent.getId(), false));
			data.getFields().add(new IndexField(Property.STRING, "parent_qname", parent.getQName().toString(), false));
			StringBuffer buff = new StringBuffer();
			while(parent != null) {
				buff.insert(0, parent.getId()+" ");
				data.appendFreeText(parent.getName());
				parent_assoc = parent.getTargetAssociation(RepositoryModel.CATEGORIES);
				if(parent_assoc == null) parent_assoc = parent.getTargetAssociation(RepositoryModel.ITEMS);
				if(parent_assoc != null && parent_assoc.getSource() != null) {
					parent = getEntityService().getEntity(parent_assoc.getSource());
				} else parent = null;
			}
			data.getFields().add(new IndexField(Property.STRING, "path", buff.toString().trim(), true));
		}
		for(Association assoc : entity.getSourceAssociations()) {			
			try {
				Entity node = assoc.getTarget() != null ? getEntityService().getEntity(assoc.getTarget()) : null;
				if(node != null) {
					QName nodeQname = node.getQName();
					if(nodeQname.equals(ClassificationModel.SUBJECT) || nodeQname.equals(ClassificationModel.PERSON) || nodeQname.equals(ClassificationModel.CORPORATION)) {
						Object nodeName = node.getProperty(SystemModel.NAME.toString());
						if(nodeName != null) data.appendFreeText(nodeName.toString());
					} else if(nodeQname.equals(SystemModel.NOTE)) {
						String type = node.hasProperty(SystemModel.NOTE_TYPE.toString()) ? node.getPropertyValue(SystemModel.NOTE_TYPE) : null;
						if(type != null) {
							if(type.equals("General note") || type.equals("Abstract") || 
									type.equals("General Physical Description note") || 
									type.equals("Table of Contents")) {
								String property = node.hasProperty(SystemModel.NOTE_CONTENT.toString()) ? node.getPropertyValue(SystemModel.NOTE_CONTENT) : null;
								if(property != null) {
									String content = property.toString();
									if(content != null && content.length() > 0) {
										data.getFields().add(new IndexField(Property.STRING, type.toLowerCase(), content, true));
										data.appendFreeText(content);
									} 
								}
							}
						}
					}
				}
			} catch(Exception e) {
				log.info("problem loading source entity id:"+assoc.getTarget()+" uid:"+assoc.getTargetUid());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void deindex(QName qname, Entity entity) {
		// TODO Auto-generated method stub
		
	}
	/*
	protected QName getQName(Long node) {
		QName qname = null;
		try {
			if(node != null) {
				String qnameStr = getNodeService().hasNodeProperty(node, "qname") ? (String)getNodeService().getNodeProperty(node, "qname") :"";
				qname = QName.createQualifiedName(qnameStr);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return qname;
	}
	*/
}
