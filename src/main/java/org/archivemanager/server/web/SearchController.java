package org.archivemanager.server.web;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.model.Result;
import org.archivemanager.util.RepositoryModelEntityBinder;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
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
	@Autowired private EntityService entityService;
	@Autowired private RepositoryModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getApplication(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/home";
	}	
	
	@RequestMapping(value = "/embedded", method = RequestMethod.GET)
	public String getEmbedded(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		return "search/include/results";
	}
	
	@ResponseBody
	@RequestMapping(value="/detail.json")
	public Result detail(@RequestParam long id) throws Exception {
		Entity entity = entityService.getEntity(id);
		return binder.getResult(entity, false);
	}
}
