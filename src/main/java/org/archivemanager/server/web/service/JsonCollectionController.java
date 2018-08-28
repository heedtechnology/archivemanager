package org.archivemanager.server.web.service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.archivemanager.model.ContentType;
import org.archivemanager.model.EnumLanguage;
import org.archivemanager.model.Result;
import org.archivemanager.server.web.model.TreeNode;
import org.archivemanager.util.RepositoryModelEntityBinder;
import org.heed.openapps.InvalidQualifiedNameException;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.cache.TimedCache;
import org.heed.openapps.crawling.Crawler;
import org.heed.openapps.crawling.CrawlerImpl;
import org.heed.openapps.crawling.CrawlingEngine;
import org.heed.openapps.crawling.CrawlingJob;
import org.heed.openapps.crawling.CrawlingModel;
import org.heed.openapps.crawling.Document;
import org.heed.openapps.crawling.DocumentResultSet;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.data.Sort;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.AssociationSorter;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.ImportProcessor;
import org.heed.openapps.entity.InvalidAssociationException;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.InvalidPropertyException;
import org.heed.openapps.entity.ModelValidationException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.ValidationResult;
import org.heed.openapps.entity.data.FileImportProcessor;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.util.JSONUtility;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.entity.EntitySorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/service/archivemanager")
public class JsonCollectionController extends WebserviceSupport {
	private TimedCache<String,Entity> entityCache = new TimedCache<String,Entity>(60);
	private TimedCache<String,FileImportProcessor> parserCache = new TimedCache<String,FileImportProcessor>(60);
	private AssociationSorter assocSort = new AssociationSorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), false));
	@Autowired private RepositoryModelEntityBinder binder;

	@ResponseBody
	@RequestMapping(value="/entity/fetch.json", method = RequestMethod.GET)
	public Result fetchEntity(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Entity entity = getEntityService().getEntity(id);
		Result result = binder.getResult(entity, true);
		return result;
	}

	@ResponseBody
	@RequestMapping(value="/collection/taxonomy.json", method = RequestMethod.GET)
	public List<TreeNode> fetchTaxonomy(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		String collectionId = req.getParameter("collection");
		String id = req.getParameter("id");
		if(id == null) {
			Entity entity = getEntityService().getEntity(Long.valueOf(collectionId));
			List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.COLLECTIONS, RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			Collections.sort(sourceAssociations, assocSort);
			if(sourceAssociations.size() == 0) {
				nodes.add(createTreeNode(entity.getQName().getLocalName(), entity.getId(), entity.getName(), "open"));
			} else {
				TreeNode collectionNode = createTreeNode(entity.getQName().getLocalName(), entity.getId(), entity.getName(), "open");
				for(int i=0; i < sourceAssociations.size(); i++) {
					Association assoc = sourceAssociations.get(i);
					if(assoc.getTargetEntity() == null) {
						assoc.setTargetEntity(getEntityService().getEntity(assoc.getTarget()));
					}
					String name = assoc.getTargetEntity().getName();
					collectionNode.getChildren().add(createTreeNode(assoc.getTargetEntity().getQName().getLocalName(),assoc.getTarget(), name, "closed"));
				}
				nodes.add(collectionNode);
			}
		} else {
			Entity entity = getEntityService().getEntity(Long.valueOf(id));
			List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			Collections.sort(sourceAssociations, assocSort);
			for(Association assoc : sourceAssociations) {
				if(assoc.getTargetEntity() == null) {
					assoc.setTargetEntity(getEntityService().getEntity(assoc.getTarget()));
				}
				String name = assoc.getTargetEntity().getName();
				if(name == null) name = assoc.getTargetEntity().getPropertyValue(RepositoryModel.DATE_EXPRESSION);
				if(name == null) name = assoc.getTargetEntity().getPropertyValue(RepositoryModel.CONTAINER);
				nodes.add(createTreeNode(assoc.getTargetEntity().getQName().getLocalName(), assoc.getTarget(), name, "closed"));
			}
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/collection/items.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchCollectionTreeData(HttpServletRequest req, HttpServletResponse res) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		String parent = req.getParameter("parent");
		String collectionId = req.getParameter("collection");
		if(parent == null || parent.equals("null")) parent = collectionId;
		if(parent == null || parent.equals("null")) {
			String query = req.getParameter("query");
			SearchRequest eQuery = (query != null) ? new SearchRequest(RepositoryModel.COLLECTION, query, "openapps_org_system_1_0_name", true) : new SearchRequest(RepositoryModel.COLLECTION, null, "openapps_org_system_1_0_name", true);
			int startRow = (req.getParameter("_startRow") != null) ? Integer.valueOf(req.getParameter("_startRow")) : 0;
			int endRow = (req.getParameter("_endRow") != null) ? Integer.valueOf(req.getParameter("_endRow")) : 75;
			eQuery.setStartRow(startRow);
			eQuery.setEndRow(endRow);
			eQuery.setFields(new String[] {"openapps_org_system_1_0_name"});
			SearchResponse collections = getSearchService().search(eQuery);
			FormatInstructions instructions = new FormatInstructions(false, false, false);
			instructions.setFormat(FormatInstructions.FORMAT_JSON);
			List<Entity> entities = new ArrayList<Entity>();
			SearchResponse results = getSearchService().search(eQuery);
			for(SearchResult result : results.getResults()) {
				entities.add(result.getEntity());
			}

			EntitySorter entitySorter = new EntitySorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), true));

			Collections.sort(entities, entitySorter);
			for(Entity collection : entities) {
				//Property name = collection.getProperty(SystemModel.NAME);
				//buff.append("<node id='"+collection.getId()+"' qname='openapps_org_repository_1_0_collection' localName='collection' parent='null'><title><![CDATA["+name.toString()+"]]></title></node>");
				data.getResponse().getData().add(getEntityService().export(instructions, collection));
			}
			if(collections.getResultSize() >= collections.getEndRow()) data.getResponse().setEndRow(collections.getEndRow());
			else data.getResponse().setEndRow(collections.getResultSize());
		} else {
			Entity collection = getEntityService().getEntity(Long.valueOf(parent));
			List<Association> list = new ArrayList<Association>();
			List<Association> sourceAssociations = collection.getSourceAssociations(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
			for(Association assoc : sourceAssociations) {
				if(assoc.getTargetEntity() == null) {
					Entity accession = getEntityService().getEntity(assoc.getTarget());
					assoc.setTargetEntity(accession);
				}
				list.add(assoc);
			}
			FormatInstructions instructions = new FormatInstructions(true, false, false);
			instructions.setFormat(FormatInstructions.FORMAT_JSON);
			for(Association component : list) {
				data.getResponse().getData().add(getEntityService().export(instructions, component));
			}

		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchCollectionAccessionTreeData(HttpServletRequest req, HttpServletResponse response) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		//res.setContentType("text/xml; charset=UTF-8");
		//res.setCharacterEncoding("UTF-8");
		String parent = req.getParameter("parent");
		String collectionId = req.getParameter("collection");
		if(parent == null || parent.equals("null")) parent = collectionId;
		int startRow = (req.getParameter("_startRow") != null) ? Integer.valueOf(req.getParameter("_startRow")) : 0;
		int endRow = (req.getParameter("_endRow") != null) ? Integer.valueOf(req.getParameter("_endRow")) : 75;
		//User user = getSecurityService().getCurrentUser(req);

		if(parent == null || parent.equals("null")) {
			String entityId = req.getParameter("entityId");
			if(entityId != null && entityId.length() > 0) {
				Entity collection = getEntityService().getEntity(Long.valueOf(entityId));
				data.getResponse().getData().add(getNodeData(String.valueOf(collection.getId()), "null", collection.getName(), "openapps_org_repository_1_0_collection", "collection"));
				data.getResponse().setTotalRows(1);
			} else {
				String query = req.getParameter("query");
				if(NumberUtility.isLong(query)) {
					Entity targetEntity = getEntityService().getEntity(Long.valueOf(query));
					data.getResponse().getData().add(getNodeData(String.valueOf(targetEntity.getId()), parent, targetEntity.getName(), targetEntity.getQName().toString(), targetEntity.getQName().getLocalName()));
					data.getResponse().setTotalRows(1);
				} else {
					SearchRequest eQuery = (query != null) ? new SearchRequest(RepositoryModel.COLLECTION, query, "openapps_org_system_1_0_name_e", false) : new SearchRequest(RepositoryModel.COLLECTION, null, "openapps_org_system_1_0_name_e", true);
					eQuery.setStartRow(startRow);
					eQuery.setEndRow(endRow);
					eQuery.setFields(new String[]{"openapps_org_system_1_0_name"});
					//if(user != null && !user.isAdministrator()) eQuery.setUser(user.getId());
					SearchResponse collections = getSearchService().search(eQuery);
					if(collections != null) {
						for(SearchResult collection : collections.getResults()) {
							data.getResponse().getData().add(getNodeData(String.valueOf(collection.getId()), "null", collection.getEntity().getName(), "openapps_org_repository_1_0_collection", "collection"));
						}
						data.getResponse().setTotalRows(collections.getResultSize());
					}
				}
			}
		} else {
			Entity collection = getEntityService().getEntity(Long.valueOf(parent));
			List<Association> list = collection.getSourceAssociations(RepositoryModel.ITEMS,RepositoryModel.CATEGORIES);
			for(Association assoc : list) {
				assoc.setTargetEntity(getEntityService().getEntity(assoc.getTarget()));
			}
			AssociationSorter sorter = new AssociationSorter(new Sort(Sort.STRING, "openapps_org_system_1_0_name", false));
			Collections.sort(list, sorter);
			FormatInstructions instructions = new FormatInstructions(true);
			instructions.setFormat(FormatInstructions.FORMAT_JSON);
			for(Association component : list) {
				if(component.getQName().equals(RepositoryModel.COLLECTION) || component.getQName().equals(RepositoryModel.ACCESSION) || component.getQName().equals(RepositoryModel.CATEGORY))
					data.getResponse().getData().add(getEntityService().export(instructions, component.getTargetEntity()));
				else {
					Entity targetEntity = component.getTargetEntity() != null ? component.getTargetEntity() : getEntityService().getEntity(component.getTarget());
					data.getResponse().getData().add(getNodeData(String.valueOf(targetEntity.getId()), parent, targetEntity.getName(), targetEntity.getQName().toString(), targetEntity.getQName().getLocalName()));
				}
			}
		}
		data.getResponse().setStartRow(startRow);
		data.getResponse().setEndRow(endRow);

		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );

		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/categories/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchCollectionCategories(HttpServletRequest req, HttpServletResponse response) throws Exception {
		StringWriter out = new StringWriter();
		String parent = req.getParameter("node");
		EntitySorter entitySorter = new EntitySorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), false));

		Entity entity = getEntityService().getEntity(Long.valueOf(parent));
		if(entity != null) {
			List<Association> seriesAssociations = entity.getSourceAssociations(RepositoryModel.CATEGORIES);
			List<Entity> series = new ArrayList<Entity>();
			for(Association seriesAssoc : seriesAssociations) {
				Entity target = getEntityService().getEntity(seriesAssoc.getTarget());
				series.add(target);
			}
			try {
				Collections.sort(series, entitySorter);
			} catch(Exception e) {
				e.printStackTrace();
			}
			for(Entity target : series) {
				out.write("{\"id\":"+target.getId()+", \"label\":"+JSONUtility.quote(target.getName())+"},");
			}
		}
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		String outResp = out.toString();
		outResp = outResp.substring(0, outResp.length() - 1);
		response.getWriter().print("["+outResp+"]");
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/collection/documents/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchCollectionDocuments(HttpServletRequest req, HttpServletResponse response) throws Exception {
		StringWriter out = new StringWriter();
		String parent = req.getParameter("id");
		//AssociationSorter associationSorter = new AssociationSorter(new Sort(Sort.STRING, SystemModel.NAME.getLocalName(), true));

		Entity entity = getEntityService().getEntity(Long.valueOf(parent));
		if(entity != null) {
			List<Entity> path = getPath(entity);
			Entity collection = path.get(0);
			List<Association> crawlersAssociations = collection.getSourceAssociations(CrawlingModel.CRAWLERS);
			for(Association crawlerAssoc : crawlersAssociations) {
				//Collections.sort(seriesAssociations, associationSorter);
				Entity crawlerEntity = getEntityService().getEntity(crawlerAssoc.getTarget());
				if(crawlerEntity != null) {
					Crawler crawler = new CrawlerImpl(crawlerEntity);
					String pathParm = crawler.getPath() + getPath(path) + "/" + entity.getName();

					SearchRequest query = new SearchRequest(CrawlingModel.DOCUMENT);
					query.getProperties().add(new Property(SystemModel.PATH, "\""+pathParm+"\""));
					SearchResponse documents = getSearchService().search(query);
					for(SearchResult document : documents.getResults()) {
						out.write("{\"id\":"+document.getId()+", \"label\":\""+document.getEntity().getName()+"\", \"path\":\""+document.getEntity().getPropertyValue(SystemModel.PATH)+"\"},");
					}
				}
			}
		}
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		String outResp = out.toString();
		if(outResp.length() > 0) {
			outResp = outResp.substring(0, outResp.length() - 1);
			response.getWriter().print("["+outResp+"]");
		} else response.getWriter().print("[]");
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/collection/add.json", method = RequestMethod.POST)
	public RestResponse<Object> add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		try {
			prepareResponse(response);

			String source = request.getParameter("source");
			String sessionKey = request.getParameter("sessionKey");
			if (source != null) {
				if (sessionKey != null && sessionKey.length() > 0) {
					Entity root = entityCache.get(sessionKey);
					if (root != null) {
						if (root.getQName().equals(RepositoryModel.COLLECTION)) {
							ImportProcessor parser = parserCache.get(sessionKey);
							Collection<Entity> entities = parser.getEntities().values();
							List<Association> associations = new ArrayList<Association>();
							int count = 1;
							for (Entity entity : entities) {
								associations.addAll(entity.getSourceAssociations());
								associations.addAll(entity.getTargetAssociations());
								entity.getSourceAssociations().clear();
								entity.getTargetAssociations().clear();
								getEntityService().addEntity(entity);
								System.out.println("processed " + count + " of " + entities.size() + " entities");
								count++;
							}
							count = 0;
							for (Association association : associations) {
								Entity sourceEntity = parser.getEntities().get(association.getSourceUid());
								Entity targetEntity = parser.getEntities().get(association.getTargetUid());
								Association assoc = getEntityService()
										.getAssociation(association.getQName(), sourceEntity.getId(),
												targetEntity.getId());
								if (assoc.getId() == null)
									getEntityService().addAssociation(assoc);
								else
									getEntityService().updateAssociation(assoc);
								System.out.println("processed " + count + " of " + parser.getAssociations().size()
										+ " associations");
								count++;
							}
						} else {
							Entity collection = getEntityService().getEntity(Long.valueOf(source));
							getEntityService().addEntity(root);
							ImportProcessor parser = parserCache.get(sessionKey);
							Collection<Entity> entities = parser.getEntities().values();
							getEntityService().addEntities(entities);
							Association assoc = new AssociationImpl(RepositoryModel.CATEGORIES,
									collection.getId(), root.getId());
							getEntityService().addAssociation(assoc);
							root.setName(root.getPropertyValue(SystemModel.NAME) + "(import)");
							getEntityService().updateEntity(root);
							ExportProcessor processor = getEntityService()
									.getExportProcessor(root.getQName().toString());
							data.getResponse().getData()
									.add(processor.export(new FormatInstructions(true), assoc));
						}
					}
				} else {
					String assocQname = request.getParameter("assoc_qname");
					String entityQname = request.getParameter("entity_qname");
					QName aQname = QName.createQualifiedName(assocQname);
					QName eQname = QName.createQualifiedName(entityQname);
					Entity entity = getEntity(request, eQname);
					ValidationResult entityResult = validate(entity);
					if (entityResult.isValid()) {
						if (entity.getId() > 0) {
							getEntityService().updateEntity(entity);
							getSearchService().update(entity);
						} else {
							getEntityService().addEntity(Long.valueOf(source), null, aQname, null, entity);
							getSearchService().update(entity);
						}
						data.getResponse().getData().add(
								getNodeData(String.valueOf(entity.getId()), source, entity.getName(),
										entity.getQName().toString(), entity.getQName().getLocalName()));
					}
				}
			} else {
				String qnameStr = request.getParameter("qname");
				QName qname = QName.createQualifiedName(qnameStr);
				Entity entity = getEntity(request, qname);
				entity.getNode().setQName(qname);
				ValidationResult result = validate(entity);
				if (result.isValid()) {
					entity.setId(getEntityService().addEntity(entity));
					getSearchService().update(entity);
					data.getResponse().getData().add(
							getNodeData(String.valueOf(entity.getId()), "null", entity.getName(),
									entity.getQName().toString(), entity.getQName().getLocalName()));

					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					response.setDateHeader("Expires", 0);
				}
			}

			data.getResponse().setStatus(0);
		} catch (Exception e){
			e.printStackTrace();
			data.getResponse().setStatus(-1);
			data.getResponse().addMessage(e.getMessage());
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateCollection(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id, @RequestParam("parent") Long parent) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Entity source = getEntityService().getEntity(id);
		Entity parentEntity = getEntityService().getEntity(parent);
		Association assoc = source.getTargetAssociation(RepositoryModel.CATEGORIES);
		if(assoc == null) assoc = source.getTargetAssociation(RepositoryModel.ITEMS);
		if(assoc == null) assoc = source.getTargetAssociation(RepositoryModel.ACCESSIONS);
		if(assoc != null && source != null && parentEntity != null) {
			assoc.setSource(parentEntity.getId());
			getEntityService().updateAssociation(assoc);
			getSearchService().update(source);
			getSearchService().update(parentEntity);

			data.getResponse().getData().add(getNodeData(String.valueOf(source.getId()), String.valueOf(parentEntity.getId()), source.getName(), source.getQName().toString(), source.getQName().getLocalName()));
		}

		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );

		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Entity entity = getEntityService().getEntity(id);
		getEntityService().removeEntity(null, id);
		getSearchService().remove(id);

		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );

		Map<String,Object> record = new HashMap<String,Object>();
		record.put("id", entity.getId());
		record.put("uid", entity.getUid());
		data.getResponse().addData(record);

		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/propagate.json", method = RequestMethod.POST)
	public RestResponse<Object> propagate(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id, @RequestParam("qname") String type, @RequestParam("value") String value) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		if(type.equals("qname")) {
			cascadeQName(id, RepositoryModel.ITEMS, QName.createQualifiedName(value));
			cascadeQName(id, RepositoryModel.CATEGORIES, QName.createQualifiedName(value));
		} else {
			getEntityService().cascadeProperty(null, id, RepositoryModel.ITEMS, QName.createQualifiedName(type), (Serializable)value);
		}
		data.getResponse().addMessage("Successfully updated child properties.");
		return data;
	}
	/*
	@ResponseBody
	@RequestMapping(value="/reindex/{id}", method = RequestMethod.GET)
	public RestResponse<Object> reindex(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();

		EntityQuery query = new EntityQuery(RepositoryModel.ITEM);
		query.setNativeQuery(new TermQuery(new Term("path", String.valueOf(id))));
		query.setEndRow(10000);
		EntityResultSet results = getEntityService().search(query);
		getLoggingService().info(JsonCollectionController.class, "reindexing "+results.getResults().size()+" entities");
		for(Entity item : results.getResults()) {
			getSearchService().update(item.getId());
		}
		data.getResponse().getMessages().add("reindexed "+results.getResults().size()+" entities");
		getLoggingService().info(JsonCollectionController.class, "reindexed "+results.getResults().size()+" entities");
		return data;
	}
	*/
	@RequestMapping(value="/collection/import/upload.json", method = RequestMethod.POST)
	public ModelAndView upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String mode = null;
		String sessionKey = "";
		try {
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload();
			// Parse the request
			FileItemIterator iter = upload.getItemIterator(req);
			byte[] file = null;
			while(iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if(item.isFormField()) {
					if(name.equals("mode")) mode = Streams.asString(stream);
					//System.out.println("Form field " + name + " with value " + Streams.asString(tmpStream) + " detected.");
				} else {
					file = IOUtils.toByteArray(stream);
				}
				FileImportProcessor parser = mode != null ? (FileImportProcessor)getEntityService().getImportProcessors(mode).get(0) : null;
				if(parser != null && file != null) {
					parser.process(new ByteArrayInputStream(file), null);
					sessionKey = java.util.UUID.randomUUID().toString();
					entityCache.put(sessionKey, parser.getRoot());
					parserCache.put(sessionKey, parser);
				}
			}
		} catch(FileUploadException e) {
			e.printStackTrace();
		}
		res.setContentType("text/html");
		res.getWriter().print("<script language='javascript' type='text/javascript'>window.top.window.uploadComplete('"+sessionKey+"');</script>");
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/collection/import/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetch(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String sessionKey) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		String source = req.getParameter("source");
		ImportProcessor parser = parserCache.get(sessionKey);
		if(parser != null) {
			if(source == null || source.equals("nodes")) {
				Entity root = entityCache.get(sessionKey);
				if(root != null) {
					printNodeTaxonomy(data.getResponse().getData(), "null", root, parser);
				}
			} else if(source.equals("node")) {
				String id = req.getParameter("id");
				if(id != null) {
					Entity node = parser.getEntityById(id);
					if(node != null) {
						FormatInstructions instr = new FormatInstructions();
						instr.setFormat(FormatInstructions.FORMAT_JSON);
						instr.setPrintSources(true);
						instr.setPrintTargets(true);
						data.getResponse().addData(getEntityService().export(instr, node));
					}
				}
			}
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/crawl.json", method = RequestMethod.POST)
	public RestResponse<Object> crawl(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") Long id) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Crawler crawler = getCrawlingService().getCrawler(id);
		CrawlingEngine engine = getCrawlingService().getEngine(crawler.getProtocol());
		CrawlingJob job = engine.crawl(crawler);

		data.getResponse().setTotalRows(job.getFilesProcessed());
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/collection/documents.json", method = RequestMethod.GET)
	public RestResponse<Object> documents(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") Long id) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Crawler crawler = getCrawlingService().getCrawler(id);
		Sort[] sort = new Sort[1];
		sort[0] = new Sort(Sort.STRING, SystemModel.PATH.toString(), true);
		int startRow = (req.getParameter("_startRow") != null) ? Integer.valueOf(req.getParameter("_startRow")) : 0;
		int endRow = (req.getParameter("_endRow") != null) ? Integer.valueOf(req.getParameter("_endRow")) : 20;

		DocumentResultSet results = getCrawlingService().getDocuments(crawler, null, startRow, endRow, sort);
		FormatInstructions instr = new FormatInstructions();
		instr.setFormat(FormatInstructions.FORMAT_JSON);
		instr.setPrintSources(true);
		instr.setPrintTargets(true);
		for(Document document : results.getResults()) {
			data.getResponse().addData(getEntityService().export(instr, (Entity)document));
		}
		data.getResponse().setStartRow(startRow);
		data.getResponse().setEndRow(endRow);
		data.getResponse().setTotalRows(results.getResultSize());
		return data;
	}

	@ResponseBody
	@RequestMapping(value="/entity/content_type.json", method = RequestMethod.GET)
	public RestResponse<Object> getContentTypes(HttpServletRequest request, HttpServletResponse res) throws Exception {

		JsonArrayBuilder builder = Json.createArrayBuilder();
		int i = 0;
		for ( ContentType c : ContentType.values()){
			JsonObjectBuilder object = Json.createObjectBuilder();
			builder.add(object.add("id", c.key()).add("text",c.value()).build());
		}

		String jsonArray = builder.build().toString();
		res.getWriter().print(jsonArray);
		return null;

	}

	@ResponseBody
	@RequestMapping(value="/entity/language.json", method = RequestMethod.GET)
	public RestResponse<Object> getEnumLanguage (HttpServletRequest request, HttpServletResponse res) throws Exception {

		JsonArrayBuilder builder = Json.createArrayBuilder();
		int i = 0;
		for ( EnumLanguage l : EnumLanguage.values()){
			JsonObjectBuilder object = Json.createObjectBuilder();
			builder.add(object.add("id", l.key()).add("text",l.value()).build());
		}

		String jsonArray = builder.build().toString();
		res.getWriter().print(jsonArray);
		return null;

	}

	protected void printNodeTaxonomy(List<Object> list, String parent, Entity node, ImportProcessor parser) throws InvalidEntityException {
		Map<String,Object> entityMap = new HashMap<String,Object>();
		entityMap.put("id", node.getUid());
		entityMap.put("name", node.getName());
		entityMap.put("parent", parent);
		for(Property property : node.getProperties()) {
			entityMap.put(property.getQName().getLocalName(), property.getValue());
		}
		if(node.getChildren().size() > 0) {
			entityMap.put("isFolder", true);
		} else {
			entityMap.put("isFolder", false);
		}
		list.add(entityMap);
		for(Association assoc : node.getChildren()) {
			Entity child = parser.getEntityById(assoc.getTargetUid());
			printNodeTaxonomy(list, node.getUid(), child, parser);
		}
	}
	protected void cascadeQName(Long id, QName association, QName qname) throws InvalidAssociationException, InvalidEntityException, InvalidPropertyException, ModelValidationException, InvalidQualifiedNameException {
		Entity entity = getEntityService().getEntity(id);
		for(Association assoc : entity.getSourceAssociations(association)) {
			Entity targetEntity = getEntityService().getEntity(assoc.getTarget());
			if(association.equals(RepositoryModel.CATEGORIES)) {
				assoc.setQname(RepositoryModel.ITEMS);
				getEntityService().updateAssociation(assoc);
				targetEntity.addProperty(RepositoryModel.CATEGORY_LEVEL, "item");
			}
			targetEntity.setQName(qname);
			getEntityService().updateEntity(targetEntity);
			cascadeQName(targetEntity.getId(), association, qname);
		}
	}
	protected Map<String,Object> getNodeData(String id, String parent, String name, String qname, String localName) {
		Map<String,Object> nodeData = new HashMap<String,Object>();
		nodeData.put("id", id);
		nodeData.put("parent", parent);
		nodeData.put("name", name);
		nodeData.put("qname", qname);
		nodeData.put("localName", localName);
		return nodeData;
	}
	protected List<Entity> getPath(Entity entity) throws Exception {
		List<Entity> entities = new ArrayList<Entity>();
		Association parent_assoc = entity.getTargetAssociation(RepositoryModel.CATEGORIES);
		if(parent_assoc == null) parent_assoc = entity.getTargetAssociation(RepositoryModel.ITEMS);
		if(parent_assoc != null && parent_assoc.getSource() != null) {
			Entity parent = getEntityService().getEntity(parent_assoc.getSource());
			while(parent != null) {
				entities.add(parent);
				parent_assoc = parent.getTargetAssociation(RepositoryModel.CATEGORIES);
				if(parent_assoc == null) parent_assoc = parent.getTargetAssociation(RepositoryModel.ITEMS);
				if(parent_assoc != null && parent_assoc.getSource() != null) {
					parent = getEntityService().getEntity(parent_assoc.getSource());
				} else parent = null;
			}
		}
		Collections.reverse(entities);
		return entities;
	}
	protected String getPath(List<Entity> entities) {
		StringWriter out = new StringWriter();
		for(int i=1; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			out.write("/" + entity.getName());
		}
		return out.toString();
	}

	private TreeNode createTreeNode(String localQName, long id, String name, String state){
		TreeNode node = new TreeNode(id, name, state);
		String iconCls = "icon-" + localQName;
		node.setIconCls(iconCls);
//		switch(localQName){
//			case "repository": node.setIconCls("icon-repository"); break;
//			case "collection": node.setIconCls("icon-collection"); break;
//			case "category": if ( state.equals("open") ) {node.setIconCls("icon-category-open");}
//											 else {node.setIconCls("icon-category-closed");} break;
//			case "audio": node.setIconCls("icon-audio"); break;
//			case "video": node.setIconCls("icon-video"); break;
//			default: node.setIconCls("icon-file");
//		}

		return node;
	}
	protected void appendRepository(HttpServletRequest request, Entity entity) throws Exception {
		String repository = request.getParameter("repository");
		if(repository != null && repository.length() > 0 && !repository.equals("null")) {
			List<Association> assocs = entity.getAssociations(RepositoryModel.COLLECTIONS);
			Entity target = getEntityService().getEntity(Long.valueOf(repository));
			if(assocs.size() == 0) {
				Association a = new AssociationImpl(RepositoryModel.COLLECTIONS, target,  entity);
				entity.getTargetAssociations().add(a);
			} else {
				Association a = assocs.get(0);
				a.setSourceEntity(target);
				a.setSource(target.getId());
				a.setTargetEntity(entity);
				a.setTarget(entity.getId());
				try {
					getEntityService().updateAssociation(a);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}