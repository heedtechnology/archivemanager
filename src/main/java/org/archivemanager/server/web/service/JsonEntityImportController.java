package org.archivemanager.server.web.service;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.archivemanager.RepositoryModel;
import org.archivemanager.server.web.model.TreeNode;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.data.IDName;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.data.Sort;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationSorter;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.ImportProcessor;
import org.heed.openapps.entity.data.FileImportProcessor;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.HttpUtility;
import org.heed.openapps.util.WebUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/service/entity")
public class JsonEntityImportController extends WebserviceSupport {
	private AssociationSorter assocSort = new AssociationSorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), false));
	private Map<String,Map<String,Entity>> entityCache = new HashMap<String,Map<String,Entity>>();
	private String qname;
	
	
	@ResponseBody
	@RequestMapping(value="/import/add.json", method = RequestMethod.POST)
	public RestResponse<Object> add(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String session) throws Exception {
		RestResponse<Object> resp = new RestResponse<Object>();		
		try {
			Map<String,Entity> data = entityCache.remove(session);			
			getEntityService().addEntities(data.values());
			resp.getResponse().setStatus(0);
			resp.getResponse().getMessages().add("Success");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	@ResponseBody
	@RequestMapping(value="/import/fetch.json", method = RequestMethod.GET)
	public List<TreeNode> fetch(@RequestParam(name="id", required=false) String id, HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();		
		Map<String,Entity> entities = entityCache.get(req.getSession().getId());
		if(entities != null) {
			if(id != null) {
				Entity entity = entities.get(id);
				nodes.addAll(outputEntity(entity));
			} else {
				for(Entity entity : entities.values()) {
					if(entity.getQName().equals(RepositoryModel.CATEGORY) && entity.getTargetAssociations().size() == 0) {
						nodes.addAll(outputEntity(entity));					
					}				
				}
			}
		}
		return nodes;
	}
	protected List<TreeNode> outputEntity(Entity entity) throws Exception {
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		List<Association> sourceAssociations = entity.getSourceAssociations(RepositoryModel.CATEGORIES, RepositoryModel.ITEMS);
		Collections.sort(sourceAssociations, assocSort);
		if(sourceAssociations.size() == 0) {
			nodes.add(new TreeNode(entity.getId(), entity.getName(), "open"));
		} else {
			for(int i=0; i < sourceAssociations.size(); i++) {
				Association assoc = sourceAssociations.get(i);
				if(assoc.getTargetEntity() == null) {
					assoc.setTargetEntity(getEntityService().getEntity(assoc.getTarget()));					
				}
				String name = assoc.getTargetEntity().getName();
				TreeNode node = assoc.getTargetEntity().getSourceAssociations().size() == 0 ?
						new TreeNode(assoc.getTarget(), name, "open") :
							new TreeNode(assoc.getTarget(), name, "closed");
				nodes.add(node);
			}			
		}
		return nodes;
	}
	@ResponseBody
	@RequestMapping(value="/import/select.json", method = RequestMethod.GET)
	public RestResponse<Object> select(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String session, @RequestParam("id") String id) throws Exception {
		RestResponse<Object> resp = new RestResponse<Object>();
		StringBuffer buff = new StringBuffer();
		Map<String,Entity> data = entityCache.get(session);
		ExportProcessor exporter = getEntityService().getExportProcessor(qname);
		if(exporter == null) exporter = getEntityService().getExportProcessor("default");
		Entity node = data.get(id);
		try {
			buff.append(exporter.export(new FormatInstructions(false), node));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	@ResponseBody
	@RequestMapping(value="/import/entity.json", method = RequestMethod.POST)
	public RestResponse<Entity> loadEntity(HttpServletRequest req, HttpServletResponse res) throws Exception {
		RestResponse<Entity> response = new RestResponse<Entity>();
		long collectionId = HttpUtility.getParmLong(WebUtility.getHttpRequest(req), "collectionId");
		if(collectionId > 0) {
			
		}
		return response;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/import/upload.json", method = RequestMethod.POST)
	public void upload(@RequestParam("file") MultipartFile file, @RequestParam("mode") String mode, @RequestParam("qname") String qname,
			HttpServletRequest req, HttpServletResponse res) throws Exception {		
		Map<String,Entity> entities = new HashMap<String,Entity>();
		
		if(mode != null && mode.length() > 0) {
		   	List<FileImportProcessor> parsers = (List)getEntityService().getImportProcessors(qname);
		   	if(parsers == null) parsers = (List)getEntityService().getImportProcessors(mode);
		   	if(parsers != null) {
		   		FileImportProcessor processor = null;
		   		for(FileImportProcessor parser : parsers) {
		   			if(parser.getId().equals(mode)) {
		   				processor = parser;
		   			}
		   		}
		   		if(processor != null) {
		   			try {
		   				processor.process(file.getInputStream(), null);
		   				entities = processor.getEntities();
		   			} catch(Exception e) {
		   				e.printStackTrace();
		   			}
		   		}
		   	}
	    } else {
	    	CSVFormat format = CSVFormat.DEFAULT;
	    	CSVParser parser = null;
	    	try {
		   		parser = new CSVParser(new InputStreamReader(file.getInputStream()), format);
	    		List<CSVRecord> records = parser.getRecords();
		   		for(CSVRecord entry : records) {
	    			Entity entity = new EntityImpl(new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE, "csv"));
	    			String uid = java.util.UUID.randomUUID().toString();
	    			entity.setUid(uid);
	    			int colCount = 0;
	    			for(String str : entry) {
	    				entity.addProperty("column"+colCount, str);
	    				colCount++;
	    			}
	    			entities.put(uid, entity);
	    		}
		   		parser.close();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		entityCache.put(req.getSession().getId(), entities);
				
	}
	/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="/import/upload.json", method = RequestMethod.POST)
	public void upload(@RequestParam("file") MultipartFile file, HttpServletRequest req, HttpServletResponse res) throws Exception {
		String qname = null;
		String sessionKey = "";
		Map<String,Entity> entities = new HashMap<String,Entity>();
		byte[] payload = null;
		String mode = null;
		// Parse the request
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> items = (List<FileItem>)upload.parseRequest(req);
			for(FileItem item : items) {
				if(!item.isFormField()) {
					payload = item.get();
				} else if(item.getFieldName().endsWith("data")) {
					String metaData = item.getString();
			    	if(metaData != null && metaData.length() > 0) {
			    		Map map = new HashMap();			    		
			    		JsonReader jsonReader = Json.createReader(new StringReader(metaData));
			    		JsonObject jsonObject = jsonReader.readObject();			    		
			    		for(String key : jsonObject.keySet()) {
			    		    map.put(key, jsonObject.get(key));
			    		}
			    		jsonReader.close();			    		
			    		qname = (String)map.get("qname");
			    	}
			    } else if(item.getFieldName().equals("mode")) {
			    	mode = item.getString();
			    }
			}
		} catch(Exception e) {
			e.printStackTrace();
	    }
		if(mode != null && mode.length() > 0) {
		   	List<FileImportProcessor> parsers = (List)getEntityService().getImportProcessors(qname);
		   	if(parsers == null) parsers = (List)getEntityService().getImportProcessors(mode);
		   	if(parsers != null) {
		   		Integer indx = Integer.valueOf(mode.substring(9));
		   		FileImportProcessor processor = parsers.get(indx);
		   		if(processor == null) {
		   			for(FileImportProcessor parser : parsers) {
		   				if(parser.getId().equals(mode)) {
		   					processor = parser;
		   				}
		   			}
		   		}
		   		if(processor != null) {
		   			try {
		   				processor.process(new ByteArrayInputStream(payload), null);
		   				entities = processor.getEntities();
		   			} catch(Exception e) {
		   				e.printStackTrace();
		   			}
		   		}
		   	}
	    } else {
	    	CSVFormat format = CSVFormat.DEFAULT;
	    	CSVParser parser = null;
	    	try {
		   		parser = new CSVParser(new InputStreamReader(new ByteArrayInputStream(payload)), format);
	    		List<CSVRecord> records = parser.getRecords();
		   		for(CSVRecord entry : records) {
	    			Entity entity = new EntityImpl(new QName(SystemModel.OPENAPPS_SYSTEM_NAMESPACE, "csv"));
	    			String uid = java.util.UUID.randomUUID().toString();
	    			entity.setUid(uid);
	    			int colCount = 0;
	    			for(String str : entry) {
	    				entity.addProperty("column"+colCount, str);
	    				colCount++;
	    			}
	    			entities.put(uid, entity);
	    		}
		   		parser.close();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		sessionKey = java.util.UUID.randomUUID().toString();
		entityCache.put(sessionKey, entities);
		res.getWriter().print("{session:\""+sessionKey+"\"}");
	}
	*/
	@ResponseBody
	@RequestMapping(value="/import/processors.json", method = RequestMethod.GET)
	public RestResponse<Object> processors(HttpServletRequest req, HttpServletResponse res, @RequestParam("namespace") String namespace, @RequestParam("localname") String localname) throws Exception {
		RestResponse<Object> resp = new RestResponse<Object>();
		qname = "{"+namespace+"}"+localname;
		List<ImportProcessor> processors = getEntityService().getImportProcessors(qname);
		for(ImportProcessor p : processors) {
			resp.getResponse().addData(new IDName(p.getId(), p.getName()));
		}
		return resp;
	}
	
}