package org.archivemanager.data;
import java.io.InputStream;
import java.util.Map;

import org.heed.openapps.QName;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.Property;
import org.heed.openapps.util.XMLUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class OAXMLCollectionImportProcessor extends CollectionImportProcessor {
	private static final long serialVersionUID = -5648783055422828169L;
	protected DataDictionaryService dictionaryService;
	
	
	
	public OAXMLCollectionImportProcessor() {
		super();
	}
	public OAXMLCollectionImportProcessor(String id, String name) {
		super(name);
	}
	
	public void process(InputStream stream, Map<String, Object> properties) throws Exception {
		getEntities().clear();
		getAssociations().clear();
		//XMLUtility.SAXParse(false, new UnicodeInputStream(stream, "UTF-8"), new ImportHandler());
		XMLUtility.SAXParse(false, stream, new ImportHandler());
		for(Association assoc : getAssociations()) {
			Entity source = getEntities().get(assoc.getSourceUid());
			Entity target = getEntities().get(assoc.getTargetUid());
			//assoc.setSource(source.getId());
			//assoc.setTarget(target.getId());
			assoc.setSourceEntity(source);
			assoc.setTargetEntity(target);
			source.getSourceAssociations().add(assoc);			
		}
	}
			
	public class ImportHandler extends DefaultHandler {
		StringBuffer buff = new StringBuffer();
		String mode = "node";
		Entity entity;
		Association association;
		Property property;
		
		public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException	{
			if(qName.equals("node")) {
				mode = "node";
				String qnameAttr =  attrs.getValue("qname");
				String uidAttr = attrs.getValue("uid");
				if(qnameAttr != null && uidAttr != null) {
					QName qname = new QName(qnameAttr);
					entity = new EntityImpl(qname);					
					entity.setUid(uidAttr);					
					if(getEntities().size() == 0) setRoot(entity);					
				}
			} else if(qName.equals("association")) {
				mode = "association";
				association = new AssociationImpl(new QName(attrs.getValue("qname")), attrs.getValue("sourceUid"), attrs.getValue("targetUid"));
			} else if(qName.equals("property")) {
				property = new Property(new QName(attrs.getValue("qname")));
				property.setType(Integer.valueOf(attrs.getValue("type")));
			}
		}
	
		public void characters(char[] ch, int start, int length) throws SAXException {
			buff.append(ch,start,length);
		}
	
		public void endElement(String namespaceURI, String sName, String qName) throws SAXException	{
			String data = buff.toString().trim();
			/** Collection Properties **/
			if(qName.equals("created")) 
				entity.setCreated(Long.valueOf(data));
			else if(qName.equals("modified")) 
				entity.setModified(Long.valueOf(data));
			else if(qName.equals("name")) 
				entity.setName(data);
			
			
			else if(qName.equals("node")) {
				getEntities().put(entity.getUid(), entity);
			} else if(qName.equals("association")) {
				getAssociations().add(association);
			} else if(qName.equals("property")) {
				if(property.getType() == 1) property.setValue(Boolean.valueOf(data));
				else if(property.getType() == 2) property.setValue(Integer.valueOf(data));
				else if(property.getType() == 3) property.setValue(Long.valueOf(data));
				else if(property.getType() == 4) property.setValue(Double.valueOf(data));
				//else if(property.getType() == 6) property.setValue(new Date(data));
				else if(property.getType() == 5 || property.getType() == 7 || property.getType() == 8) 
					property.setValue(data);
				
				if(mode.equals("node")) entity.getProperties().add(property);
				else if(mode.equals("association")) association.getProperties().add(property);
			}
			buff = new StringBuffer();
		}
		
	}
	protected void addProperty(Entity entity, QName qname, Object data) {
		try {
			entity.addProperty(qname, data);
		} catch(Exception e) {
			System.out.println("error adding property qname:"+qname+" value:"+data);
		}
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	
}
