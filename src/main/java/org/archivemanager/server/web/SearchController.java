package org.archivemanager.server.web;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.data.SystemModelEntityBinder;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/search")
public class SearchController {
	@Autowired private SearchService searchService;
	@Autowired private SystemModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getUsers(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/home";
	}	
	
	/** Services **/
	@ResponseBody
	@RequestMapping(value="/search.json")
	public RestResponse<User> search(@RequestParam(required=false) String query, @RequestParam(required=false, defaultValue="1") int page, 
			@RequestParam(required=false, defaultValue="20") int size) throws Exception {
		RestResponse<User> data = new RestResponse<User>();		
		int start = (page*size) -  size;
		int end = page*size;
		data.setStartRow(start);
		data.setEndRow(end);
		SearchRequest request = new SearchRequest(SystemModel.USER);
		SearchResponse results = searchService.search(request);
		for(SearchResult result : results.getResults()) {
			data.addRow(binder.getUser(result.getEntity()));
		}
		data.setTotal(results.getResultSize());
		return data;
	}
}
