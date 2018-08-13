package org.archivemanager.search.indexing;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.archivemanager.search.parsing.date.DateParser;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.ModelField;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.indexing.EntityIndexer;
import org.heed.openapps.entity.indexing.IndexEntity;
import org.heed.openapps.entity.indexing.IndexFacet;
import org.heed.openapps.entity.indexing.IndexField;


public class DefaultEntityIndexer implements EntityIndexer {
	private final static Logger log = Logger.getLogger(DefaultEntityIndexer.class.getName());
	private DataDictionaryService dictionaryService;
	private EntityService entityService;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
	
	public void index(Entity entity, IndexEntity data) throws InvalidEntityException {
		StringBuffer freeText = new StringBuffer();
		if(entity != null) {
			String id = String.valueOf(entity.getId());
			if(id != null) {			
				DataDictionary dictionary = dictionaryService.getDataDictionary(entity.getDictionary());
				//data.getFields().add(new IndexField(Property.LONG, "id", entity.getId(), false));
				data.getFields().add(new IndexField(Property.STRING, "uuid", entity.getUid(), false));
				
				Property name = entity.getProperty(SystemModel.NAME.toString());
				if(name != null) {
					data.getFields().add(new IndexField(Property.STRING, SystemModel.NAME.toString(), name.getValue(), true));
					//data.getFields().add(new IndexField(Property.STRING, SystemModel.NAME.toString()+"_e", name.getValue(), false));
					appendFreeText(freeText, (String)name.getValue());
				} else if(entity.getName() != null) {
					data.getFields().add(new IndexField(Property.STRING, SystemModel.NAME.toString(), entity.getName(), true));
					//data.getFields().add(new IndexField(Property.STRING, SystemModel.NAME.toString()+"_e", entity.getName(), false));
					appendFreeText(freeText, entity.getName());
				}
				try {
					List<QName> qnames = dictionary.getQNames(entity.getQName());
					if(qnames.size() > 1) {
						String[] array = new String[qnames.size()];
						for(int i=0; i < qnames.size(); i++) {
		        			array[i] = qnames.get(i).toString();
		        		}
						data.getFields().add(new IndexField(Property.STRING, "qname", array, false));
					} else data.getFields().add(new IndexField(Property.STRING, "qname", qnames.get(0).toString(), false));
					
				} catch(Exception e) {
					throw new InvalidEntityException("", e);
				}
				
				if(entity.getCreated() > 0) data.getFields().add(new IndexField(Property.LONG, "created", entity.getCreated(), false));
				if(entity.getModified() > 0) data.getFields().add(new IndexField(Property.LONG, "modified", entity.getModified(), false));
				data.getFields().add(new IndexField(Property.BOOLEAN, "deleted", entity.getDeleted(), false));
				if(entity.getUser() > 0) data.getFields().add(new IndexField(Property.LONG, "user", entity.getUser(), false));
				for(Property property : entity.getProperties()) {
					Object propertyValue = property.getValue();
					if(propertyValue != null) {
						if(propertyValue instanceof String) {
							String textVal = (String)propertyValue;
							if(textVal.length() > 0) {
								if(property.getType() == Property.DATE) {
									data.getFields().add(new IndexField(Property.STRING, property.getQName().toString(), textVal, false));
									Date date_value = parseTextDate(textVal);
									if(date_value != null) {
										long epoch = date_value.getTime();
										data.getFields().add(new IndexField(Property.LONG, property.getQName().toString()+"_", epoch, false));
									} else {
										data.getFields().add(new IndexField(Property.LONG, property.getQName().toString()+"_", -99999999999999L, false));
									}
								} else if(property.getType() == Property.DOUBLE) {
									data.getFields().add(new IndexField(Property.DOUBLE, property.getQName().toString(), Double.valueOf(textVal), false));
								} else if(property.getType() == Property.INTEGER) {
									data.getFields().add(new IndexField(Property.INTEGER, property.getQName().toString(), Integer.valueOf(textVal), false));
								} else if(textVal.length() > 0) {
									if(!property.getQName().equals(SystemModel.NAME) && !property.getQName().equals(SystemModel.DESCRIPTION)) 
										appendFreeText(freeText, textVal);
									else {
										try {
											ModelField field = dictionary.getModelField(entity.getQName(), property.getQName());
											if(field != null) {
												data.getFields().add(new IndexField(property.getType(), property.getQName().toString(), textVal, field.isTokenized()));
												if(field.isSearchable())
													appendFreeText(freeText, textVal);
											}
										} catch(Exception e) {
											throw new InvalidEntityException("", e);
										}
									}
								}
							}
						} else {
							data.getFields().add(new IndexField(property.getType(), property.getQName().toString(), String.valueOf(property.getValue()), false));
						}
					}
				}
				
				List<Long> associations = new ArrayList<Long>();
				for(Association assoc : entity.getSourceAssociations()) {
					if(associations.contains(assoc.getTarget())) {
						//log.info("duplicate source association for id:"+assoc.getTarget()+" qname:"+assoc.getQName().toString());
					} else {
						//log.info("added source association for id:"+assoc.getTarget()+" qname:"+assoc.getQName().toString());
						Entity assoc_entity = entityService.getEntity(assoc.getTarget());
						if(assoc_entity.getName() != null)
							data.getFacets().add(new IndexFacet(assoc.getQName(), assoc_entity.getName(), assoc.getTarget()));
						associations.add(assoc.getTarget());
					}
				}
				Long[] source_ids = associations.toArray(new Long[associations.size()]);
				data.getFields().add(new IndexField(Property.LONG, SystemModel.SOURCE_ASSOC.getLocalName(), source_ids, true));
				associations.clear();
				for(Association assoc : entity.getTargetAssociations()) {
					if(associations.contains(assoc.getSource())) {
						//log.info("duplicate target association for id:"+assoc.getSource()+" qname:"+assoc.getQName().toString());
					} else {
						//log.info("added target association for id:"+assoc.getSource()+" qname:"+assoc.getQName().toString());
						Entity assoc_entity = entityService.getEntity(assoc.getSource());
						if(assoc_entity.getName() != null)
							data.getFacets().add(new IndexFacet(assoc.getQName(), assoc_entity.getName(), assoc.getSource()));
						associations.add(assoc.getSource());
					}
				}
				Long[] target_ids = associations.toArray(new Long[associations.size()]);
				data.getFields().add(new IndexField(Property.LONG, SystemModel.TARGET_ASSOC.getLocalName(), target_ids, true));
				/** Access Control List **/
				List<Association> permissionAssocs = entity.getSourceAssociations(SystemModel.PERMISSIONS);
				if(permissionAssocs != null && permissionAssocs.size() > 0) {
					for(Association assoc : permissionAssocs) {
						Entity permission = entityService.getEntity(assoc.getTarget());
						String nodeid = permission.getPropertyValue(SystemModel.PERMISSION_TARGET);
						if(nodeid != null && !nodeid.equals("null")) 
							data.getFields().add(new IndexField(Property.LONG, "acl", Long.valueOf(nodeid), true));
						else log.log(Level.SEVERE, "no permission target available on association : "+assoc.toJsonObject().toString());
					}
					
				} else data.getFields().add(new IndexField(Property.LONG, "acl", 0, true));				
				data.appendFreeText(freeText.toString().trim());
			}			
		}
	}
	public void deindex(QName qname, Entity entity)	throws InvalidEntityException {
		if(entity != null) {
			try {
				//nodeService.deindexNode(entity.getId(), qname.toString());
			} catch(Exception e) {
				log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	private DateParser parser = new DateParser();
	protected Date parseTextDate(String in) {
		String dateStr = cleanDate(in);
		Date date = parser.parse(dateStr);
		if(date == null && !in.toLowerCase().equals("undated")) 
			log.info("problem parsing "+in);
		else
			log.info(in+" parsed to "+dateFormat.format(date)+" and epoch "+date.getTime());
		return date;
	}
	protected String cleanDate(String in) {
		in = in.replace("circa ", "");
		in = in.replace("after ", "");
		in = in.replace("received ", "");
		in = in.replace("postmarked ", "");
		in = in.replace(".", "");
		in = in.replace("[", "");
		in = in.replace("]", "");
		if(in.contains(",") && (in.contains("/") || in.contains("-"))) {
			String[] dates = in.split(",");
			if(dates.length > 0) return dates[0].trim();
		}
		return in;
	}	
	protected void appendFreeText(StringBuffer freeText, String text) {
		if(text == null) return;
		String cleanText = text.replace(",", "").replace("\"", "").replace(";", "").replace(".", "").replace(":", "");
		String[] parts = cleanText.split(" ");
		for(String part : parts) {
			if(freeText.indexOf(part) == -1) 
				freeText.append(part.toLowerCase()+" ");
		}
	}
	
	public DataDictionaryService getDictionaryService() {
		return dictionaryService;
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	public EntityService getEntityService() {
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	
}
