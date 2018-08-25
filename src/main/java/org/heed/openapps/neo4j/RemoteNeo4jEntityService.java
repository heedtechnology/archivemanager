package org.heed.openapps.neo4j;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.cache.CacheService;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.DataDictionaryException;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.dictionary.ModelField;
import org.heed.openapps.dictionary.ModelRelation;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.EntityPersistenceListener;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.ImportProcessor;
import org.heed.openapps.entity.InvalidAssociationException;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.InvalidPropertyException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;


public class RemoteNeo4jEntityService implements EntityService {
	private static final long serialVersionUID = 7870373534164732513L;
	private final static Logger log = Logger.getLogger(RemoteNeo4jEntityService.class.getName());
	private CacheService cacheService;
	private DataDictionaryService dictionaryService;
	private Driver driver;

	private Map<String, List<ImportProcessor>> importers = new HashMap<String, List<ImportProcessor>>();
	private Map<String, ExportProcessor> exporters = new HashMap<String, ExportProcessor>();
	private Map<String, EntityPersistenceListener> persistenceListeners = new HashMap<String, EntityPersistenceListener>();


	public RemoteNeo4jEntityService(String host, String user, String pass) {
		driver = GraphDatabase.driver( "bolt://"+host+":7687", AuthTokens.basic(user, pass));
	}

	public void shutdown() {
		driver.close();
	}
	
	public int count(QName qname) {
		Session session = driver.session();
		String query = "MATCH (n1) WHERE exists(n1.openapps_org_system_1_0_name) and n1.qname='"+qname.toString()+"' return count(*)";
		try {
			StatementResult countResult = session.run(query);
			while(countResult.hasNext()) {
				Record record = countResult.next();
				return record.get(0).asInt();
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return 0;
	}
	
	public EntityResultSet getEntities(QName qname, int page, int size) {
		long startTime = System.currentTimeMillis();
		EntityResultSet entities = new EntityResultSet();
		int start = (page*size) -  size;
		Session session = driver.session();
		String query = "MATCH (n1) WHERE exists(n1.openapps_org_system_1_0_name) and n1.qname='"+qname.toString()+"'";		
		try {
			StatementResult countResult = session.run(query+" return count(*)");
			while(countResult.hasNext()) {
				Record record = countResult.next();
				entities.setSize(record.get(0).asInt());
			}
			String resultQuery = query+" return n1";
			if(start > 0) resultQuery += " skip "+start;
			if(size > 0) resultQuery += " limit "+size;
			StatementResult mainResult = session.run(resultQuery);			
			org.neo4j.driver.v1.types.Node n = null;
			while(mainResult.hasNext()) {
				Record record = mainResult.next();
				n = record.get("n1").asNode();
				Entity entity = getEntity(n.id());
				entities.getData().add(entity);					
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		log.info("entity search {qname="+qname.toString()+",page="+page+",size="+size+"} completed in "+(System.currentTimeMillis() - startTime)+" ms");
		return entities;
	}
	/*
	public EntityResultSet getEntities(QName qname, int page, int size) {
		long startTime = System.currentTimeMillis();
		EntityResultSet results = new EntityResultSet();
		int start = (page*size) -  size;
		Session session = driver.session();	
		StatementResult countResult = session.run("MATCH (n1) WHERE exists(n1.openapps_org_system_1_0_name) and n1.qname='"+qname.toString()+"' return count(*)");
		while(countResult.hasNext()) {
			Record record = countResult.next();
			results.setSize(record.get(0).asInt());
		}
		String idQuery = "MATCH (n1) WHERE exists(n1.openapps_org_system_1_0_name) and n1.qname='"+qname.toString()+"' return n1";
		if(start > 0) idQuery += " skip "+start;
		if(size > 0) idQuery += " limit "+size;
		try {
			List<Long> ids = new ArrayList<Long>();			
			StatementResult idResult = session.run(idQuery);
			while(idResult.hasNext()) {
				Record record = idResult.next();
				org.neo4j.driver.v1.types.Node n = record.get("n1").asNode();
				ids.add(n.id());
			}
			String resultQuery = "MATCH (n1)-[r]-(n2) WHERE ID(n1) IN [";
			for(Long id : ids) {
				resultQuery += id+",";
			}
			resultQuery = resultQuery.substring(0, resultQuery.length() - 1) + "] return n1,r,n2";
			Map<Long,Entity> entities = new HashMap<Long,Entity>();
			StatementResult mainResult = session.run(resultQuery);			
			while(mainResult.hasNext()) {
				Record record = mainResult.next();
				org.neo4j.driver.v1.types.Node n = record.get("n1").asNode();
				if(n != null) {
					Entity entity = entities.get(n.id());
					if(entity == null) entity = getEntity(n);
					if(entity != null) {
						getEntityRelationship(entity, record);
						entities.put(n.id(), entity);
					}
				}				
			}
			results.getData().addAll(entities.values());
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			//session.close();
		}
		log.info("entity search {qname="+qname.toString()+",page="+page+",size="+size+"} completed in "+(System.currentTimeMillis() - startTime)+" ms");
		return results;
	}
	*/
	@Override
	public EntityResultSet getEntities(QName qname, QName propertyQname, Object value, int page, int size) throws InvalidEntityException {
		EntityResultSet entities = new EntityResultSet();
		int start = (page*size) -  size;
		int end = page*size;
		Session session = driver.session();
		StatementResult result = session.run("MATCH (n) WHERE n."+qname.toString()+"='"+value+"' return n, count(*);");
		try {
			int count = 0;
			org.neo4j.driver.v1.types.Node n = null;
			while(result.hasNext()) {
				if(count >= start) {
					Record record = result.next();
					n = record.get("n1").asNode();
					Entity entity = getEntity(n.id());
					entities.getData().add(entity);					
				}
				if(count >= end) break;
				count++;
			}			
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return entities;
	}
	public Entity getEntity(long id) throws InvalidEntityException {
		/* TODO commented out due to problems with caches when adding associations. Sourcenode is not being added to cache with updated assocs */
		//Entity entity = entity = (Entity) cacheService.get("node", String.valueOf(id));
		Entity entity = null;
		if(entity == null) {
			Session session = driver.session();
			StatementResult result = session.run("MATCH (n1)-[r]-(n2) WHERE ID(n1)="+id+" return n1,r,n2;");
			try {
				org.neo4j.driver.v1.types.Node n = null;
				while(result.hasNext()) {
					Record record = result.next();
					if(entity == null) {
						n = record.get("n1").asNode();
						if(n.id() == id)
							entity = getEntity(n);
					}
					if(entity != null) getEntityRelationship(entity, record);
				}
				if(entity == null) {
					StatementResult result2 = session.run("MATCH (n1) WHERE ID(n1)="+id+" return n1;");
					while(result2.hasNext()) {
						Record record = result2.next();
						n = record.get("n1").asNode();
						if(n.id() == id)
							entity = getEntity(n);
					}
				}
			} catch(Exception e) {
				log.log(Level.SEVERE, "", e);
			} finally {
				//session.close();
			}
			cacheService.put("node", String.valueOf(id), entity);
		}
		return entity;
	}


	public Entity getEntity(QName qname, long id) throws InvalidEntityException {
		return getEntity(id);
	}
	public Entity getEntity(String uid) throws InvalidEntityException {
		Session session = driver.session();
		StatementResult result = session.run("MATCH (n) WHERE n.uid='"+uid+"' return n;");
		org.neo4j.driver.v1.types.Node n = null;
		while(result.hasNext()) {
			Record record = result.next();
			if(n == null) {
				n = record.get("n1").asNode();
				return getEntity(n.id());
			}
		}
		return null;
	}
	
	@Override
	public Entity getEntity(QName qname, QName propertyQname, Object value) throws InvalidEntityException {
		Session session = driver.session();
		StatementResult result = session.run("MATCH (n) WHERE n."+qname.toString()+"='"+value+"' return n;");
		org.neo4j.driver.v1.types.Node n = null;
		while(result.hasNext()) {
			Record record = result.next();
			if(n == null) {
				n = record.get("n1").asNode();
				return getEntity(n.id());
			}
		}
		return null;
	}
	protected void getEntityRelationship(Entity entity, Record record) throws Exception {
		Node n = record.get("n1").asNode();
		org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
		if(r.startNodeId() == n.id()) {
			Association association = new AssociationImpl(QName.createQualifiedName(r.type()), r.startNodeId(), r.endNodeId());
			association.setId(r.id());
			for(String key : r.keys()) {
				Value val = r.get(key);
				association.addProperty(QName.createQualifiedName(key), val.asObject());

			}
			org.neo4j.driver.v1.types.Node targetNode = record.get("n2").asNode();
			Entity targetEntity = getEntity(targetNode);
			if(targetEntity != null) {
				association.setTargetEntity(targetEntity);
				association.setTargetName(targetEntity.getQName());
				association.setSourceName(entity.getQName());
				entity.getSourceAssociations().add(association);
			} else log.log(Level.INFO, "missing target entity : "+targetNode.id());
		} else {
			Association association = new AssociationImpl(QName.createQualifiedName(r.type()), r.startNodeId(), r.endNodeId());
			association.setId(r.id());
			for(String key : r.keys()) {
				Value val = r.get(key);
				association.addProperty(QName.createQualifiedName(key), val.asObject());
			}
			org.neo4j.driver.v1.types.Node sourceNode = record.get("n2").asNode();
			Entity sourceEntity = getEntity(sourceNode);
			if(sourceEntity != null) {
				association.setSourceEntity(sourceEntity);
				association.setSourceName(sourceEntity.getQName());
				association.setTargetName(entity.getQName());
				entity.getTargetAssociations().add(association);
			} else log.log(Level.INFO, "missing source entity : "+sourceNode.id());
		}
	}
	protected Entity getEntity(org.neo4j.driver.v1.types.Node n) throws Exception {
		DataDictionary dictionary = dictionaryService.getSystemDictionary();
		Value qnameValue = n.get("qname");
		QName qname = QName.createQualifiedName(qnameValue.asString());
		Model model = dictionary.getModel(qname);
		Entity node = new EntityImpl(n.id(), qname);
//		for ( ModelField field : model.getFields()){
//			field.
//		}
		if(model != null) {
			for(String key : n.keys()) {
				Value val = n.get(key);
				if(key.equals("uid") || key.equals("uuid")) node.setUid(val.asString());
				else if(key.equals(SystemModel.NAME.toString())) node.setName(val.asString());
				else if(key.equals("description")) node.addProperty(SystemModel.DESCRIPTION, val.asString());
				else if(key.equals("qname")) node.setQName(QName.createQualifiedName(val.asString()));
				else if(key.equals("accessed")) node.setAccessed(val.asLong());
				else if(key.equals("created")) node.setCreated(val.asLong());
				else if(key.equals("creator")) node.setCreator(val.asLong());
				else if(key.equals("modified")) node.setModified(val.asLong());
				else if(key.equals("modifier")) node.setModifier(val.asLong());
				else if(key.equals("xid")) node.setXid(val.asLong());
				else if(key.equals("user")) node.setUser(val.asLong());
				else if(key.equals("index")) node.addProperty(new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE, "index"), val.asObject());
				else if(key.equals("deleted")) node.setDeleted(val.asBoolean());
				else {
					try {
						QName q = QName.createQualifiedName(key);
						ModelField field = model.getField(q);
						node.addProperty(field.getPropertyType(), q, val.asObject());
					} catch(Exception e) {
						node.addProperty(key, val.asObject());
					}
				}
			}
			for(ModelField field : model.getFields() ){
				if (Objects.isNull(node.getProperty(field.getQName()))){
					node.addProperty(field.getPropertyType(),field.getQName(),null);
				}
			}
			return node;
		} else {
			log.log(Level.SEVERE, "missing data dictionary model for : "+qname);
			/*
			Session session = driver.session();
			StatementResult result = session.run("MATCH (n) WHERE ID(n) = "+n.id()+" DELETE n;");
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Node deletedNode = record.get("r").asNode();
				log.log(Level.INFO, "deleted entity:"+deletedNode.id());
			}
			*/
		}
		return null;
	}

	public Association getAssociation(long id) throws InvalidAssociationException {
		Association association = null;
		Session session = driver.session();
		StatementResult result = session.run("MATCH (n1)-[r]->(n2) WHERE id(r) = "+id+" return r;");
		try {
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				association = new AssociationImpl(QName.createQualifiedName(r.type()), r.startNodeId(), r.endNodeId());
				association.setId(r.id());
				for(String key : r.keys()) {
					Value val = r.get(key);
					association.addProperty(QName.createQualifiedName(key), val.asObject());

				}
				org.neo4j.driver.v1.types.Node sourceNode = record.get("n1").asNode();
				org.neo4j.driver.v1.types.Node targetNode = record.get("n2").asNode();
				Entity sourceEntity = getEntity(sourceNode);
				Entity targetEntity = getEntity(targetNode);
				association.setSourceEntity(sourceEntity);
				association.setSourceName(sourceEntity.getQName());
				association.setTargetEntity(targetEntity);
				association.setTargetName(targetEntity.getQName());
				break;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return association;
	}

	public Association getAssociation(QName qname, long source, long target) throws InvalidAssociationException {
		Association association = null;
		Session session = driver.session();
		String query = "MATCH (n1)-[r]->(n2) WHERE id(n1) = "+source+" and id(n2) = "+target+" and r.qname = '"+qname.toString()+"' return n1,r,n2;";
		StatementResult result = session.run(query);
		try {
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				association = new AssociationImpl(QName.createQualifiedName(r.type()), r.startNodeId(), r.endNodeId());
				association.setId(r.id());
				for(String key : r.keys()) {
					Value val = r.get(key);
					association.addProperty(QName.createQualifiedName(key), val.asObject());

				}
				org.neo4j.driver.v1.types.Node sourceNode = record.get("n1").asNode();
				org.neo4j.driver.v1.types.Node targetNode = record.get("n2").asNode();
				Entity sourceEntity = getEntity(sourceNode);
				Entity targetEntity = getEntity(targetNode);
				association.setSourceEntity(sourceEntity);
				association.setSourceName(sourceEntity.getQName());
				association.setTargetEntity(targetEntity);
				association.setTargetName(targetEntity.getQName());
				break;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			session.close();
		}
		return association;
	}

	public Long addAssociation(Association association) throws InvalidAssociationException {
		if((association.getSource() == null && association.getSourceEntity() == null) || (association.getTarget() == null && association.getTargetEntity() == null))
			return null;
		DataDictionary dictionary = dictionaryService.getDataDictionary(association.getDictionary());
		EntityPersistenceListener startListener = persistenceListeners.get(association.getSourceName().toString());
		if(startListener == null) startListener = persistenceListeners.get("default");
		EntityPersistenceListener endListener = persistenceListeners.get(association.getTargetName().toString());
		if(endListener == null) endListener = persistenceListeners.get("default");

		Session session = driver.session();
		String query = "MATCH (n1),(n2) WHERE id(n1) = "+association.getSource()+" and id(n2) = "+association.getTarget();
		query += " CREATE (n1)-[r:"+association.getQName().toString()+"]->(n2) return r;";

		//startListener.onBeforeAssociationAdd(association);
		//endListener.onBeforeAssociationAdd(association);

		StatementResult result = session.run(query, toMap(association,dictionary));
		try	{
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				association.setId(r.id());
			}
		} catch(Exception e) {
			throw new InvalidAssociationException("", e);
		} finally {
			session.close();
		}
		//startListener.onAfterAssociationAdd(association);
		//endListener.onAfterAssociationAdd(association);

		cacheService.put("node", String.valueOf(association.getSource()), association.getSourceEntity());
		return association.getId();
	}

	public void addEntities(Collection<Entity> entities) throws InvalidEntityException {
		Map<String,Long> idMap = new HashMap<String,Long>();
		try	{
			int i = 0;
			for(Entity entity : entities) {
				addEntity(entity);
				idMap.put(entity.getUid(), entity.getId());
				i++;
				if(i % 10 == 0) System.out.println(i+" of "+entities.size()+" nodes migrated");
			}
			i = 0;
			for(Entity entity : entities) {
				List<Association> sourceAssociations = entity.getSourceAssociations();
				for(Association assoc : sourceAssociations) {
					try	{
						//String qname = assoc.getQName().toString();
						Long sourceid = entity.getId();
						Long targetid = assoc.getTarget();
						if(targetid == null) targetid = idMap.get(assoc.getTargetUid());
						if(sourceid != null && targetid != null) {
							assoc.setSource(sourceid);
							assoc.setTarget(targetid);
							addAssociation(assoc);
						} else {
							System.out.println("bad news on id lookup:"+sourceid+" to "+targetid);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				List<Association> targetAssociations = entity.getTargetAssociations();
				for(Association assoc : targetAssociations) {
					try	{
						//String qname = assoc.getQName().toString();
						Long sourceid = assoc.getSource();
						Long targetid = entity.getId();
						if(sourceid == null) sourceid = idMap.get(assoc.getSourceUid());
						if(sourceid != null && targetid != null) {
							assoc.setSource(sourceid);
							assoc.setTarget(targetid);
							addAssociation(assoc);
						} else {
							System.out.println("bad news on id lookup:"+sourceid+" to "+targetid);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				i++;
				if(i % 10 == 0) System.out.println(i+" of "+entities.size()+" nodes relationships migrated");
			}
		} catch(InvalidEntityException e) {
			throw new InvalidEntityException("", e);
		}
	}

	public Long addEntity(Entity entity) throws InvalidEntityException {
		try	{
			DataDictionary dictionary = dictionaryService.getDataDictionary(entity.getDictionary());
			if(entity.getQName() == null) throw new InvalidEntityException("entity model not found for missing qname");
			EntityPersistenceListener listener = persistenceListeners.get(entity.getQName().toString());
			if(listener == null) listener = persistenceListeners.get("default");
			if(listener != null) listener.onBeforeAdd(entity);

			Session session = driver.session();
			Map<String,Object> map = toMap(entity, dictionary);
			StatementResult result = session.run("CREATE (n $map) return n;", map);
			org.neo4j.driver.v1.types.Node n = null;
			while(result.hasNext()) {
				Record record = result.next();
				n = record.get("n").asNode();
				return n.id();
			}
			/*
			List<Association> sourceAssociations = entity.getSourceAssociations();
			for(Association assoc : sourceAssociations) {
				if(assoc.getId() == null) {
					assoc.setSourceEntity(entity);
					addAssociation(assoc);
				}
			}
			List<Association> targetAssociations = entity.getTargetAssociations();
			for(Association assoc : targetAssociations) {
				if(assoc.getId() == null) {
					assoc.setTargetEntity(entity);
					addAssociation(assoc);
				}
			}
			*/
			if(listener != null) {
				listener.onAfterAdd(entity);
			}
			cacheService.put("entityCache", String.valueOf(entity.getId()), entity);
		} catch(Exception e) {
			e.printStackTrace();
			throw new InvalidEntityException("entity model not found for qname:"+entity.getQName().toString(), e);
		}
		return entity.getId();
	}
	protected Map<String,Object> toMap(Entity entity, DataDictionary dictionary) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(entity.getUid() == null || entity.getUid().length() == 0) entity.setUid(java.util.UUID.randomUUID().toString());
		Map<String,Object> properties = new HashMap<String,Object>();
		map.put("map", properties);
		properties.put("uid", entity.getUid());
		properties.put("accessed", entity.getAccessed());
		properties.put("created", entity.getCreated());
		properties.put("creator", entity.getCreator());
		properties.put("modified", entity.getModified());
		properties.put("modifier", entity.getModifier());
		properties.put("xid", entity.getXid());
		properties.put("user", entity.getUser());
		properties.put("deleted", entity.getDeleted());
		properties.put("qname", entity.getQName().toString());
		properties.put(SystemModel.NAME.toString(), entity.getName());
		try {
			List<ModelField> fields = dictionary.getModelFields(entity.getQName());
			for(Property property : entity.getProperties()) {
				ModelField field = null;
				for(ModelField f : fields) {
					if(f.getQName().equals(property.getQName())) {
						field = f;
						break;
					}
				}
				if(field != null) {
					if(property.getValue() != null) {
						properties.put(property.getQName().toString(), property.getValue());
					}
				} else if(property.getType() == Property.COMPUTED) {
					properties.put(property.getQName().toString(), property.getValue());
				} else log.log(Level.INFO, "Model field not found for : " + property.getQName().toString());
			}
		} catch(DataDictionaryException e) {
			log.log(Level.SEVERE, "", e);
		}
		return map;
	}
	protected Map<String,Object> toMap(Association association, DataDictionary dictionary) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			ModelRelation model = dictionary.getModelRelation(association.getSourceName(), association.getQName());
			List<ModelField> fields = model.getFields();
			for(Property property : association.getProperties()) {
				ModelField field = null;
				for(ModelField f : fields) {
					if(f.getQName().equals(property.getQName())) {
						field = f;
						break;
					}
				}
				if(field != null) {
					if(property.getValue() != null) {
						map.put(property.getQName().toString(), property.getValue());
					}
				} else if(property.getType() == Property.COMPUTED) {
					map.put(property.getQName().toString(), property.getValue());
				} else log.log(Level.INFO, "Model field not found for : " + property.getQName().toString());
			}
		} catch(DataDictionaryException e) {
			log.log(Level.SEVERE, "", e);
		}
		return map;
	}

	public Long addEntity(Long source, Long target, QName association, List<Property> properties, Entity entity) throws InvalidEntityException {
		try	{
			entity.setId(addEntity(entity));
			Association assoc = null;
			if(source != null) assoc = getAssociation(association, source, entity.getId());
			else if(target != null) assoc = getAssociation(association, entity.getId(), target);

			if ( source != null && assoc == null && target == null ) {
				assoc = new AssociationImpl(association, source, entity.getId());
				Entity sourceEntity = getEntity(source);
				assoc.setSourceEntity(sourceEntity);
				assoc.setSourceName(sourceEntity.getQName());
				assoc.setTargetEntity(entity);
				assoc.setTargetName(entity.getQName());

			} else {
				throw new InvalidEntityException("invalid association source:"+source+" target:"+target);
			}

			if(assoc != null) {
				if (properties != null) {
					for (Property property : properties) {
						assoc.getProperties().add(property);
					}
				}
				addAssociation(assoc);
			}
			return assoc.getId();
		} catch(InvalidAssociationException e1) {
			e1.printStackTrace();
		}

		cacheService.put("entityCache", String.valueOf(entity.getId()), entity);
		return entity.getId();
	}

	public void updateAssociation(Association association) throws InvalidAssociationException {
		try	{
			//Node startNode = association.getSource() != null ? nodeService.getNode(association.getSource()) : nodeService.getNode(association.getSourceEntity().getId());
			//Node endNode = association.getTarget() != null ? nodeService.getNode(association.getTarget()) : nodeService.getNode(association.getTargetEntity().getId());
			Entity sourceEntity = getEntity(association.getSource());
			Entity targetEntity = getEntity(association.getTarget());

			association.setSourceName(sourceEntity.getQName());
			association.setTargetName(targetEntity.getQName());

			EntityPersistenceListener startListener = persistenceListeners.get(association.getSourceName().toString());
			if(startListener == null) startListener = persistenceListeners.get("default");
			EntityPersistenceListener endListener = persistenceListeners.get(association.getTargetName().toString());
			if(endListener == null) endListener = persistenceListeners.get("default");

			startListener.onBeforeAssociationUpdate(association);
			endListener.onBeforeAssociationUpdate(association);

			removeAssociation(association.getId());
			addAssociation(association);

			startListener.onAfterAssociationUpdate(association);
			endListener.onAfterAssociationUpdate(association);
		} catch(Exception e) {
			throw new InvalidAssociationException("", e);
		}
	}

	public void updateEntity(Entity entity) throws InvalidEntityException {
		try	{
			DataDictionary dictionary = dictionaryService.getDataDictionary(entity.getDictionary());
			EntityPersistenceListener listener = persistenceListeners.get(entity.getQName().toString());
			if(listener == null) listener = persistenceListeners.get("default");
			if(listener != null) {
				Entity oldValue = getEntity(entity.getId());
				listener.onBeforeUpdate(oldValue, entity);
			}
			if(entity.getId() != null) {
				Session session = driver.session();
				Map<String,Object> map = toMap(entity, dictionary);
				StatementResult result = session.run("MATCH (n) WHERE ID(n) = "+entity.getId()+" SET n=$map return n;", map);
				org.neo4j.driver.v1.types.Node n = null;
				while(result.hasNext()) {
					Record record = result.next();
					n = record.get("n").asNode();
					log.log(Level.INFO, "updated node:"+n.id());
				}
				for(Association assoc : entity.getSourceAssociations()) {
					if(assoc.getId() == null) {
						assoc.setSourceEntity(entity);
						addAssociation(assoc);
					}
				}
				for(Association assoc : entity.getTargetAssociations()) {
					if(assoc.getId() == null) {
						assoc.setTargetEntity(entity);
						addAssociation(assoc);
					}
				}
				//cacheService.remove("entityCache", String.valueOf(entity.getId()));
			}
			if(listener != null) {
				listener.onAfterUpdate(entity);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void cascadeProperty(QName qname, Long id, QName association, QName propertyName, Serializable propertyValue) throws InvalidEntityException, InvalidPropertyException {
		try	{
			Entity entity = getEntity(id);
			List<Association> associations = entity.getSourceAssociations(association);
			for(Association assoc : associations) {
				Entity targetEntity = getEntity(assoc.getTarget());
				targetEntity.addProperty(propertyName, propertyValue);
				updateEntity(targetEntity);
				cascadeProperty(targetEntity.getQName(), targetEntity.getId(), association, propertyName, propertyValue);
			}
		} catch(Exception e) {
			throw new InvalidEntityException("", e);
		}
	}

	public void removeAssociation(long id) throws InvalidAssociationException {
		Session session = driver.session();
		try	{
			Association association = getAssociation(id);
			Entity source = association.getSourceEntity() != null ? association.getSourceEntity() : getEntity(association.getSource());
			Entity target = association.getTargetEntity() != null ? association.getTargetEntity() : getEntity(association.getTarget());
			association.setSourceName(source.getQName());
			association.setTargetName(target.getQName());

			EntityPersistenceListener startListener = persistenceListeners.get(association.getSourceName().toString());
			if(startListener == null) startListener = persistenceListeners.get("default");
			EntityPersistenceListener endListener = persistenceListeners.get(association.getTargetName().toString());
			if(endListener == null) endListener = persistenceListeners.get("default");
			if(Objects.nonNull(startListener)) startListener.onBeforeAssociationDelete(association);
			if(Objects.nonNull(endListener)) endListener.onBeforeAssociationDelete(association);

			StatementResult result = session.run("MATCH [r] WHERE ID(r) = "+association.getId()+" DELETE r;");
			while(result.hasNext()) {
				Record record = result.next();
				org.neo4j.driver.v1.types.Relationship r = record.get("r").asRelationship();
				log.log(Level.INFO, "deleted association:"+r.id());
			}
			if(Objects.nonNull(startListener)) startListener.onAfterAssociationDelete(association);
			if(Objects.nonNull(endListener)) endListener.onAfterAssociationDelete(association);
			//cacheService.remove("entityCache", String.valueOf(association.getSource()));
			//cacheService.remove("entityCache", String.valueOf(association.getTarget()));
		} catch(Exception e) {
			throw new InvalidAssociationException("", e);
		} finally {
			session.close();
		}
	}

	public void removeEntity(QName qname, long id) throws InvalidEntityException {
		Session session = driver.session();
		try	{
			/** Cascade the delete to entities marked so in the data dictionary
			 *  regardless, delete them from the cache so they are reloaded on next request. **/
			Entity entity = getEntity(id);
			if(entity != null) {
				DataDictionary dictionary = dictionaryService.getDataDictionary(entity.getDictionary());
				if(dictionary != null) {
					EntityPersistenceListener listener = persistenceListeners.get(entity.getQName().toString());
					if(listener != null) listener.onBeforeDelete(entity);

					List<QName> cascades = new ArrayList<QName>();
					Model model = dictionary.getModel(entity.getQName());
					while(model != null) {
						for(ModelRelation relation : model.getSourceRelations()) {
							if(relation.isCascade()) {
								cascades.add(relation.getQName());
							}
						}
						model = model.getParent();
					}
					if(cascades.size() > 0) {
						List<Long> processedIds = new ArrayList<Long>();
						for(Association assoc : entity.getSourceAssociations()) {
							if(cascades.contains(assoc.getQName())) {
								if(assoc.getTarget() != id && !processedIds.contains(assoc.getTarget())) {
									removeEntity(assoc.getTargetName(), assoc.getTarget());
									processedIds.add(assoc.getTarget());
									//cacheService.remove("entityCache", String.valueOf(assoc.getTarget()));
								}
							}
						}
					}
					for(Association assoc : entity.getTargetAssociations()) {
						removeAssociation(assoc.getId());
					}

					StatementResult result = session.run("MATCH (n) WHERE ID(n) = "+entity.getId()+" DELETE n;");
					while(result.hasNext()) {
						Record record = result.next();
						org.neo4j.driver.v1.types.Node n = record.get("r").asNode();
						log.log(Level.INFO, "deleted entity:"+n.id());
					}

					log.log(Level.INFO,"removing node:"+entity.getId());
					if(listener != null) listener.onAfterDelete(entity);
				}
			}
		} catch(Exception e) {
			throw new InvalidEntityException("", e);
		} finally {
			session.close();
		}
	}

	public Object export(FormatInstructions instructions, Entity entity) throws InvalidEntityException {
		ExportProcessor defaultProcessor = getExportProcessor("default");
		ExportProcessor processor = getExportProcessor(entity.getQName().toString());
		if(processor != null) {
			return processor.export(instructions, entity);
		} else if ( Objects.nonNull(defaultProcessor) ){
			return defaultProcessor.export(instructions, entity);
		}
		return entity;
	}

	public Object export(FormatInstructions instructions, Association association)	throws InvalidEntityException {
		ExportProcessor defaultProcessor = getExportProcessor("default");
		ExportProcessor processor = getExportProcessor(association.getTargetName().toString());
		if(processor != null) {
			return processor.export(instructions, association);
		} else {
			return defaultProcessor.export(instructions, association);
		}
	}

	public void registerImportProcessor(String name, ImportProcessor processor) {
		List<ImportProcessor> list = importers.get(name);
		if(list == null) {
			list = new ArrayList<ImportProcessor>();
			importers.put(name, list);
		}
		list.add(processor);
	}
	public void registerExportProcessor(String name, ExportProcessor processor) {
		exporters.put(name, processor);
	}

	public List<ImportProcessor> getImportProcessors(String name) {
		List<ImportProcessor> processors = new ArrayList<ImportProcessor>();
		for(String key : importers.keySet()) {
			if(key.startsWith(name) || key.equals("default")) {
				List<ImportProcessor> procs = importers.get(key);
				processors.addAll(procs);
			}
		}
		return processors;
	}

	public ExportProcessor getExportProcessor(String name) {
		ExportProcessor processor = exporters.get(name);
		if(processor != null) return processor;
		else return exporters.get("default");
	}

	public EntityPersistenceListener getEntityPersistenceListener(String name) {
		EntityPersistenceListener listener = persistenceListeners.get(name);
		if(listener == null ) listener = persistenceListeners.get("default");
		return listener;
	}
	public void setImporters(Map<String, List<ImportProcessor>> importers) {
		this.importers = importers;
	}
	public void addExporter(String name, ExportProcessor exporter) {
		exporters.put(name, exporter);
	}
	public void setExporters(Map<String, ExportProcessor> exporters) {
		this.exporters = exporters;
	}
	public void addPersistenceListener(String name, EntityPersistenceListener listener) {
		persistenceListeners.put(name, listener);
	}
	public void setPersistenceListeners(Map<String, EntityPersistenceListener> persistenceListeners) {
		this.persistenceListeners = persistenceListeners;
	}
	public void registerEntityPersistenceListener(String name, EntityPersistenceListener component) {
		persistenceListeners.put(name, component);
	}
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

}
