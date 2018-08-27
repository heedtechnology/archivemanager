package org.archivemanager.server.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.RepositoryModel;
import org.archivemanager.data.RestResponse;
import org.archivemanager.model.Result;
import org.archivemanager.util.RepositoryModelEntityBinder;
import org.heed.openapps.QName;
import org.heed.openapps.User;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.data.SearchRestResponse;
import org.heed.openapps.security.GuestUser;
import org.heed.openapps.util.NumberUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service/search")
public class SearchServiceController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(SearchServiceController.class.getName());
	@Autowired private RepositoryModelEntityBinder binder;
	
	
	@ResponseBody
	@RequestMapping(value="/entity.json", method = RequestMethod.POST)
	public SearchRestResponse<Result> search(@RequestParam(required=false) String query, @RequestParam(required=false) QName qname, 
			@RequestParam(required=false, defaultValue="0") int page, @RequestParam(required=false, defaultValue="20") int rows,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchRestResponse<Result> data = new SearchRestResponse<Result>();
		if(qname == null) qname = RepositoryModel.COLLECTION;
		SearchRequest entityQuery = new SearchRequest(qname, query);
		entityQuery.setStartRow((page * rows)-rows);
		entityQuery.setEndRow(page * rows);
		SearchResponse results = getSearchService().search(entityQuery);		
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
			for(SearchResult entity : results.getResults()) {
				data.addRow(binder.getResult(entity.getEntity(), true));
			}
			data.setAttributes(results.getAttributes());
			data.setBreadcrumb(results.getBreadcrumb());
		}		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/entityQuery.json", method = RequestMethod.POST)
	public RestResponse<Entity> search(@RequestBody SearchRequest query, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//EntityQuery query = new EntityQuery();
		//query.fromJson(requestData);
		
		if(query.getUser() != null) {
			User user = getSecurityService().getUserByUsername(query.getUser().getUsername());
			if(user == null) {
				user = getSecurityService().addUser(query.getUser());
			}
			query.setUser(user);
		} else query.setUser(new GuestUser());
		
		String sourcesStr = request.getParameter("sources");
		String targetsStr = request.getParameter("targets");
		
		boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
		boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;
		
		prepareResponse(response);
		
		RestResponse<Entity> data = new RestResponse<Entity>();
		long startTime = System.currentTimeMillis();
		
		SearchResponse results = null;
		List<Entity> entities = new ArrayList<Entity>();
		if(query != null && !query.equals("null")) {
			if(NumberUtility.isLong(query.getQuery())) {
				try {
					Entity entity = getEntityService().getEntity(Long.valueOf(query.getQuery()));
					if(entity != null) {
						entities.add(entity);
						data.setStartRow(0);
						data.setEndRow(1);
						data.setTotal(1);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(query.equals("orphans")) {
				log.info("received orphans query...");
				List<Entity> list = new ArrayList<Entity>();
				//EntityQuery eQuery = new EntityQuery(query.getEntityQnames(), null, sort, false);
				query.setStartRow(0);
				query.setEndRow(10000);
				results = getSearchService().search(query);
				for(SearchResult entity : results.getResults()) {
					if(entity.getEntity().getTargetAssociations().size() == 0)
						list.add(entity.getEntity());
				}
				for(int i=query.getStartRow(); i < query.getEndRow(); i++) {
					if(list.size() > i) entities.add(list.get(i));
				}
				data.setStartRow(query.getStartRow());
				if(list.size() >= results.getEndRow()) data.setEndRow(results.getEndRow());
				else data.setEndRow(list.size());
				data.setTotal(list.size());
			} else {
				results = getSearchService().search(query);
				for(SearchResult entity : results.getResults()) {
					entities.add(entity.getEntity());
				}
			}
		} else {
			results = getSearchService().search(query);
			for(SearchResult entity : results.getResults()) {
				entities.add(entity.getEntity());
			}
		}		
		FormatInstructions instr = new FormatInstructions();
		instr.setFormat(FormatInstructions.FORMAT_JSON);
		instr.setPrintSources(sources);
		instr.setPrintTargets(targets);		
		try {
			for(Entity entity : entities) {
				//data.getResponse().addData(getEntityService().export(instr, entity));
				data.addRow(entity);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}		
		data.setTime(System.currentTimeMillis() - startTime);
		if(results != null) {
			data.setStartRow(results.getStartRow());
			if(results.getResultSize() >= results.getEndRow()) data.setEndRow(results.getEndRow());
			else data.setEndRow(results.getResultSize());
			data.setTotal(results.getResultSize());
		}		
		if(query.getQnames().size() > 1)	
			data.addMessage((entities.size()+" "+query.getQnames().get(0).getLocalName()+" fetched"));
		else {
			String msg = "";
			for(QName q : query.getQnames())
				msg += q.toString()+" ";
			data.addMessage((entities.size()+" "+msg.trim()+" fetched"));
		}
		return data;
	}
}
