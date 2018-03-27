package org.archivemanager.server.web;
import java.util.Map;
import java.util.logging.Logger;

import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.search.EntityQuery;
import org.heed.openapps.search.EntityResultSet;
import org.heed.openapps.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/service/search")
public class SearchController {
	private final static Logger log = Logger.getLogger(SearchController.class.getName());
	@Autowired private SearchService searchService;
	
	@GetMapping("/default")
	public String getDefault(Map<String, Object> model) {
		
		return "search/default";
	}
	@GetMapping("/collections")
	public String getCollections(@RequestParam(defaultValue="",required=false) String query, 
			@RequestParam(required=false) String code, 
			@RequestParam(defaultValue="0",required=false) int start, 
			@RequestParam(defaultValue="10",required=false) int end, 
			@RequestParam(required=false) String sort, 
			Map<String, Object> model) {
		if(query == null || query.length() == 0) query = "all results";
		String[] sorts = (sort != null) ? sort.split(",") : new String[0];
		
		EntityQuery searchRequest = getSearchRequest(RepositoryModel.COLLECTION, query, start, end, sorts, false);
		searchRequest.setType(EntityQuery.TYPE_SEARCH);
		searchRequest.addParameter(RepositoryModel.CODE.getLocalName(), code);
		
		EntityResultSet results = searchService.search(searchRequest);
		model.put("results", results);
		
		return "/search/collections";
	}
	
	protected EntityQuery getSearchRequest(QName qname, String query, int startRow, int endRow, String[] sorts, boolean attributes) {
		EntityQuery sQuery = new EntityQuery(qname, query);
		sQuery.setFetchAttributes(attributes);
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sorts != null) {
			for(String sort : sorts) {
				Sort lSort = null;
				String[] sortStrings = sort.split(",");
				for(String sortStr : sortStrings) {
					String[] s = sortStr.split(" ");
					if(s.length == 2) {
						boolean reverse = s[1].equals("asc") ? true : false;
						if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, s[0], reverse);
						else lSort = new Sort(Sort.STRING, s[0], reverse);						
					} else if(s.length == 1) {
						if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, s[0], true);
						else lSort = new Sort(Sort.STRING, s[0], false);
					}
					sQuery.addSort(lSort);
				}
			}
		}				
		return sQuery;
	}
}
