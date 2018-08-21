package org.archivemanager.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archivemanager.model.Audio;
import org.archivemanager.model.Category;
import org.archivemanager.model.Collection;
import org.archivemanager.model.DigitalObject;
import org.archivemanager.model.Entry;
import org.archivemanager.model.Item;
import org.archivemanager.model.NamedEntity;
import org.archivemanager.model.Note;
import org.archivemanager.model.PathNode;
import org.archivemanager.model.Subject;
import org.archivemanager.model.Video;
import org.archivemanager.model.WebLink;
import org.archivemanager.RepositoryModel;
import org.archivemanager.data.ResultSorter;
import org.heed.openapps.Permission;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.content.ContentModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.archivemanager.model.Result;
import org.heed.openapps.util.HTMLUtility;
import org.jsoup.Jsoup;


public class RepositoryModelEntityBinder {
	private EntityService entityService;
	private ResultSorter sorter = new ResultSorter();
	
	public RepositoryModelEntityBinder(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public Result getResult(Entity entity, boolean sources) {
		Result result = null;
		if(entity.getQName().equals(RepositoryModel.COLLECTION))
			result = getCollection(entity, sources);
		else if(entity.getQName().equals(ClassificationModel.ENTRY))
			result = getEntry(entity);
		else if(entity.getQName().equals(ClassificationModel.PERSON) || entity.getQName().equals(ClassificationModel.CORPORATION))
			result = getNamedEntity(entity);
		else if(entity.getQName().equals(ClassificationModel.SUBJECT))
			result = getSubject(entity);
		else if(entity.getQName().equals(RepositoryModel.CATEGORY))
			result = getCategory(entity);
		else 
			result = getItem(entity);
		if(result != null) {
			result.setUser(entity.getUser());			
		}
		return result;
	}
	public Collection getCollection(Entity entity, boolean sources) {
		long id = entity.getId();
		String title = decode(entity.getName());
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		if(description == null) description = "";
		Collection collection = new Collection(id, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.URL) && entity.getPropertyValue(RepositoryModel.URL).length() > 0) 
			collection.setUrl(entity.getPropertyValue(RepositoryModel.URL));
		if(entity.hasProperty(RepositoryModel.BIOGRAPHICAL_NOTE)) collection.setBioNote(entity.getPropertyValue(RepositoryModel.BIOGRAPHICAL_NOTE));
		if(entity.hasProperty(RepositoryModel.SCOPE_NOTE)) collection.setScopeNote(entity.getPropertyValue(RepositoryModel.SCOPE_NOTE));
				
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			collection.setDateExpression(entity.getPropertyValue(RepositoryModel.DATE_EXPRESSION));
			if(collection.getName() == null) collection.setName(collection.getDateExpression());			
		}
		if(entity.hasProperty(RepositoryModel.BEGIN_DATE)) collection.setBegin(entity.getPropertyValue(RepositoryModel.BEGIN_DATE));
		if(entity.hasProperty(RepositoryModel.END_DATE)) collection.setEnd(entity.getPropertyValue(RepositoryModel.END_DATE));		
		if(entity.hasProperty(RepositoryModel.CONTAINER) ) collection.setContainer(entity.getPropertyValue(RepositoryModel.CONTAINER));		
		if(entity.hasProperty(RepositoryModel.COMMENT) ) collection.setComment(entity.getPropertyValue(RepositoryModel.COMMENT));
		if(entity.hasProperty(RepositoryModel.CODE) ) collection.setCode(entity.getPropertyValue(RepositoryModel.CODE));
		if(entity.hasProperty(RepositoryModel.IDENTIFIER) ) collection.setIdentifier(entity.getPropertyValue(RepositoryModel.IDENTIFIER));
		if(entity.hasProperty(RepositoryModel.ACCESSION_DATE) ) collection.setAccessionDate(entity.getPropertyValue(RepositoryModel.ACCESSION_DATE));
		if(entity.hasProperty(RepositoryModel.BULK_BEGIN) ) collection.setBulkBegin(entity.getPropertyValue(RepositoryModel.BULK_BEGIN));
		if(entity.hasProperty(RepositoryModel.BULK_END) ) collection.setBulkEnd(entity.getPropertyValue(RepositoryModel.BULK_END));
		if(entity.hasProperty(RepositoryModel.LANGUAGE) ) collection.setLanguage(entity.getPropertyValue(RepositoryModel.LANGUAGE));
		if(entity.hasProperty(RepositoryModel.EXTENT_UNITS) ) collection.setExtentUnits(entity.getPropertyValue(RepositoryModel.EXTENT_UNITS));
		if(entity.hasProperty(RepositoryModel.EXTENT_NUMBER) ) collection.setExtentNumber(entity.getPropertyValue(RepositoryModel.EXTENT_NUMBER));
		if(entity.hasProperty(RepositoryModel.RESTRICTIONS) ) collection.setRestrictions(entity.getBooleanPropertyValue(RepositoryModel.RESTRICTIONS));
		if(entity.hasProperty(RepositoryModel.INTERNAL) ) collection.setInternal(entity.getBooleanPropertyValue(RepositoryModel.INTERNAL));
		if(entity.hasProperty(RepositoryModel.IS_PUBLIC) ) collection.setPublic(entity.getBooleanPropertyValue(RepositoryModel.IS_PUBLIC));
		
		if(sources && entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(targetEntity != null) {	
						if(association.getQName().getLocalName().equals("categories")) {
							Category category = getCategory(targetEntity);
							collection.getCategories().add(category);
						} else if(association.getQName().getLocalName().equals("notes")) {					
							if(targetEntity.hasProperty(SystemModel.NOTE_TYPE)) {
								String type = targetEntity.getPropertyValue(SystemModel.NOTE_TYPE);
								String content = targetEntity.getPropertyValue(SystemModel.NOTE_CONTENT);
								collection.getNotes().add(new Note("", type, decode(content)));
								if(type.equals("Abstract")) collection.setAbstractNote(decode(content));
							}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(targetEntity);
							if(localName.equals("person")) collection.getPeople().add(name);
							if(localName.equals("corporation")) collection.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(targetEntity);
							collection.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(targetEntity);
							collection.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(targetEntity);
							collection.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("permissions")) {
							Permission permission = getPermission(targetEntity);
							collection.getPermissions().add(permission);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}				
		return collection;
	}
	public Permission getPermission(Entity entity) {
		Permission permission = new Permission();
		permission.setId(entity.getId());
		permission.setLevel(Integer.valueOf(entity.getPropertyValue(SystemModel.PERMISSION_LEVEL)));
		permission.setName(entity.getName());
		permission.setSource(Long.valueOf(entity.getPropertyValue(SystemModel.PERMISSION_SOURCE)));
		permission.setTarget(Long.valueOf(entity.getPropertyValue(SystemModel.PERMISSION_TARGET)));
		return permission;
	}
	public Category getCategory(Entity entity) {
		Category category = new Category(entity.getId(), entity.getName());
		category.setContentType("category");
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(association.getQName().getLocalName().equals("items")) {
						Item item = getItem(targetEntity);
						category.getItems().add(item);
					} else if(association.getQName().getLocalName().equals("categories")) {
						Category childCategory = getCategory(targetEntity);
						category.getCategories().add(childCategory);
					}					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		return category;
	}
	public Entry getEntry(Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Entry entry = new Entry(id, title, description, contentType);
		
		if(entity.hasProperty(ClassificationModel.COLLECTION_NAME)) 
			entry.setCollection(entity.getPropertyValue(ClassificationModel.COLLECTION_NAME));
		if(entity.hasProperty(ClassificationModel.ITEMS)) {
			String itemDescription = (String)entity.getProperty(ClassificationModel.ITEMS).getValue();
			itemDescription = itemDescription.replace("\n", "<br>");
			entry.setDescription(itemDescription);
		}
		
		if(entity.getTargetAssociations().size() > 0) {
			for(int i=0; i < entity.getTargetAssociations().size(); i++) {
				Association association = entity.getTargetAssociations().get(i);
				if(association.getQName().getLocalName().equals("entries")) {
					Entity sourceEntity = association.getSourceEntity();
					try {
						if(sourceEntity == null) sourceEntity = entityService.getEntity(null, association.getSource());
						entry.setNamedEntity(String.valueOf(association.getSource()));
						if(sourceEntity != null) {											
							entry.setName(sourceEntity.getName());
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return entry;
	}
	public Item getItem(Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Item item = new Item(id, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.LANGUAGE)) item.setLanguage(getLanguageLabel(entity.getPropertyValue(RepositoryModel.LANGUAGE)));
		else item.setLanguage("English");
		if(entity.hasProperty(RepositoryModel.CONTAINER)) item.setContainer(entity.getPropertyValue(RepositoryModel.CONTAINER));
		if(entity.hasProperty(RepositoryModel.SUMMARY) && entity.getPropertyValue(RepositoryModel.SUMMARY).length() > 0) item.setSummary(entity.getPropertyValue(RepositoryModel.SUMMARY));
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			if(item.getName() == null) item.setName(entity.getPropertyValue(RepositoryModel.DATE_EXPRESSION));
			item.setDateExpression(entity.getPropertyValue(RepositoryModel.DATE_EXPRESSION));
		}

		Map<String,String> entityPropMap = new HashMap<>();
		entity.getPropertyMap().forEach((key, object) -> entityPropMap.put(key,(String)object));
		item.setData(entityPropMap);

		List<Entity> path = getComponentPath(entity);
		if(path.size() > 0) {
			for(int i=0; i < path.size(); i++) {
				Entity node = path.get(i);
				PathNode pathNode = new PathNode(String.valueOf(node.getId()), node.getName());
				item.getPath().add(pathNode);
				if(node.getQName().equals(RepositoryModel.COLLECTION)) {
					item.setCollectionId(String.valueOf(node.getId()));
					item.setCollectionName(node.getName());
					item.setCollectionUrl(node.getPropertyValue(new QName("openapps.org_repository_1.0", "url")));
				}
			}
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(targetEntity != null) {	
						if(association.getQName().getLocalName().equals("notes")) {					
							if(targetEntity.hasProperty(SystemModel.NOTE_TYPE)) {
							String type = targetEntity.getPropertyValue(SystemModel.NOTE_TYPE);
							String content = targetEntity.getPropertyValue(SystemModel.NOTE_CONTENT);
							if(type.equals("General note") || type.equals("General Physical Description note") ||
								type.equals("Table of Contents")) {
								item.getNotes().add(new Note("", type, content));
							}
							if(type.equals("Abstract")) item.setAbstractNote(decode(content));
							if(type.equals("Language of Materials note")) {
								item.setNativeContent(content);
							}
						}					
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(targetEntity);
							if(localName.equals("person")) item.getPeople().add(name);
							if(localName.equals("corporation")) item.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(targetEntity);
							item.getSubjects().add(subject);
						} else if(association.getQName().getLocalName().equals("files")) {
							DigitalObject file = getDigitalObject(targetEntity);
							item.getDigitalObjects().add(file);
						} else if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(targetEntity);
							item.getWeblinks().add(file);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(item.getPeople(), sorter);
		Collections.sort(item.getCorporations(), sorter);
		Collections.sort(item.getSubjects(), sorter);
		return item;
	}
	public NamedEntity getNamedEntity(Entity entity) {
		long id = entity.getId();
		String title = HTMLUtility.removeTags(entity.getName());
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		NamedEntity name = new NamedEntity(id, title, description, contentType);
		
		if(entity.hasProperty(RepositoryModel.FUNCTION)) name.setFunction(entity.getPropertyValue(RepositoryModel.FUNCTION));
		if(entity.hasProperty(ClassificationModel.RULE)) name.setRole(entity.getPropertyValue(ClassificationModel.RULE));
		if(entity.hasProperty(ClassificationModel.NOTE)) name.setNote(entity.getPropertyValue(ClassificationModel.NOTE));
		if(entity.hasProperty(ClassificationModel.DATES)) name.setDates(entity.getPropertyValue(ClassificationModel.DATES));
		if(entity.hasProperty(ClassificationModel.SOURCE)) name.setSource(entity.getPropertyValue(ClassificationModel.SOURCE));
				
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				if(association.getQName().getLocalName().equals("entries")) {
					Entity targetEntity = association.getTargetEntity();
					try {
						if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
						Entry entry = getEntry(targetEntity);
						name.getEntries().add(entry);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(entity.getTargetAssociations().size() > 0) {
			for(int i=0; i < entity.getTargetAssociations().size(); i++) {
				Association association = entity.getTargetAssociations().get(i);
				if(association.getQName().getLocalName().equals("named_entities")) {
					Entity sourceEntity = association.getSourceEntity();
					try {
						if(sourceEntity == null) sourceEntity = entityService.getEntity(null, association.getSource());
						if(sourceEntity.getQName().equals(RepositoryModel.COLLECTION)) {
							Collection collection = getCollection(sourceEntity, false);
							name.getCollections().add(collection);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return name;
	}
	public Audio getAudio(Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Audio audio = new Audio(id, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			audio.setDateExpression(entity.getPropertyValue(RepositoryModel.DATE_EXPRESSION));
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(targetEntity != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(targetEntity);
							audio.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(targetEntity);
							if(localName.equals("person")) audio.getPeople().add(name);
							if(localName.equals("corporation")) audio.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(targetEntity);
							audio.getSubjects().add(subject);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(WebLink link : audio.getWeblinks()) {
			if(link.getType().equals("avatar"))
				audio.setAvatar(link.getUrl());
			else if(link.getType().equals("rendition"))
				audio.setRendition(link.getUrl());
		}
		return audio;
	}
	public Subject getSubject(Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValue(SystemModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Subject subject = new Subject(id, title, description, contentType);
		if(entity.hasProperty(ClassificationModel.TYPE)) subject.setType(entity.getPropertyValue(ClassificationModel.TYPE));
		if(entity.hasProperty(ClassificationModel.SOURCE)) subject.setSource(entity.getPropertyValue(ClassificationModel.SOURCE));
		List<Association> collectionAssociations = entity.getTargetAssociations(RepositoryModel.COLLECTION);
		for(Association collectionAssociation : collectionAssociations) {
			Entity sourceEntity = collectionAssociation.getSourceEntity();
			try {
				if(sourceEntity == null) sourceEntity = entityService.getEntity(null, collectionAssociation.getSource());
				if(sourceEntity != null) {
					Collection collection = getCollection(sourceEntity, false);
					subject.getCollections().add(collection);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return subject;
	}
	public Video getVideo(Entity entity) {
		long id = entity.getId();
		String title = entity.getName();
		String description = entity.getPropertyValue(RepositoryModel.DESCRIPTION);
		String contentType = cleanContentType(entity.getQName().getLocalName());
		Video video = new Video(id, title, description, contentType);
		if(entity.hasProperty(RepositoryModel.DATE_EXPRESSION)) {
			video.setDateExpression(entity.getPropertyValue(RepositoryModel.DATE_EXPRESSION));
		}
		if(entity.getSourceAssociations().size() > 0) {
			for(int i=0; i < entity.getSourceAssociations().size(); i++) {
				Association association = entity.getSourceAssociations().get(i);
				Entity targetEntity = association.getTargetEntity();
				try {
					if(targetEntity == null) targetEntity = entityService.getEntity(null, association.getTarget());
					if(targetEntity != null) {
						if(association.getQName().getLocalName().equals("web_links")) {
							WebLink file = getWebLink(targetEntity);
							video.getWeblinks().add(file);
						} else if(association.getQName().getLocalName().equals("named_entities")) {
							String localName = targetEntity.getQName().getLocalName();
							NamedEntity name = getNamedEntity(targetEntity);
							if(localName.equals("person")) video.getPeople().add(name);
							if(localName.equals("corporation")) video.getCorporations().add(name);
						} else if(association.getQName().getLocalName().equals("subjects")) {
							Subject subject = getSubject(targetEntity);
							video.getSubjects().add(subject);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		for(WebLink link : video.getWeblinks()) {
			if(link.getType().equals("avatar"))
				video.setAvatar(link.getUrl());
			else if(link.getType().equals("rendition"))
				video.setRendition(link.getUrl());
		}
		return video;
	}
	public DigitalObject getDigitalObject(Entity entity) {
		DigitalObject file = new DigitalObject();
		file.setId(String.valueOf(entity.getId()));
		file.setUuid(entity.getUid());
		file.setTitle(entity.getName());						
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getPropertyValue(ContentModel.TYPE));
		//if(source.has("group")) file.setGroup(source.getString("group"));
		//if(source.has("order")) file.setOrder(source.getInt("order"));
		return file;
	}
	public WebLink getWebLink(Entity entity) {
		WebLink file = new WebLink();
		file.setId(String.valueOf(entity.getId()));
		if(entity.hasProperty(SystemModel.URL)) {
			file.setUrl(entity.getPropertyValue(SystemModel.URL));
			int endIdx = file.getUrl().indexOf("/", 7);
			if(endIdx > -1) file.setDomain(file.getUrl().substring(0, endIdx));
		}
		file.setTitle(entity.getName());
		if(entity.hasProperty(ContentModel.TYPE)) file.setType(entity.getPropertyValue(ContentModel.TYPE));
		return file;
	}
	protected String decode(String in) {
		if(in == null) return "";
		return Jsoup.parse(in).text();//in.replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", "").replace("<br>", "");
	}
	protected List<Entity> getComponentPath(Entity comp) {
		List<Entity> path = new ArrayList<Entity>();
		if(comp != null) {
			Association parent = comp.getTargetAssociation(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			while(parent != null && parent.getSource() != null) {
				try {
					Entity p = entityService.getEntity(null, parent.getSource());
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
	protected String getLanguageLabel(String in) {
		if(in.equals("en")) return "English";
		if(in.equals("ar")) return "Arabic";
		if(in.equals("zh")) return "Chinese";
		if(in.equals("cs")) return "Czech";
		if(in.equals("da")) return "Danish";
		if(in.equals("nl")) return "Dutch";
		if(in.equals("en")) return "English";
		if(in.equals("fi")) return "Finnish";
		if(in.equals("fr")) return "French";
		if(in.equals("de")) return "German";
		if(in.equals("el")) return "Greek";
		if(in.equals("he")) return "Hebrew";
		if(in.equals("hu")) return "Hungarian";
		if(in.equals("is")) return "Icelandic";
		if(in.equals("it")) return "Italian";
		if(in.equals("ja")) return "Japanese";
		if(in.equals("ko")) return "Korean";
		if(in.equals("no")) return "Norwegian";
		if(in.equals("pl")) return "Polish";
		if(in.equals("pt")) return "Portugese";
		if(in.equals("ru")) return "Russian";
		if(in.equals("es")) return "Spanish";
		if(in.equals("sv")) return "Swedish";
		if(in.equals("th")) return "Thai";
		if(in.equals("tr")) return "Turkish";
		if(in.equals("yi")) return "Yiddish";
		return in;
	}
	protected String cleanContentType(String in) {
		if(in == null) return "";
		if(in.equals("audio")) return "Audio";
		if(in.equals("correspondence")) return "Correspondence";
		if(in.equals("financial")) return "Financial Material";
		if(in.equals("legal")) return "Legal Material";
		if(in.equals("manuscript")) return "Manuscript";
		if(in.equals("memorabilia")) return "Memorabilia";
		if(in.equals("photographs")) return "Photographic Material";
		if(in.equals("printed_material")) return "Printed Material";
		if(in.equals("professional")) return "Professional Material";
		if(in.equals("video")) return "Video";
		if(in.equals("research")) return "Research";
		return in;
	}
}
