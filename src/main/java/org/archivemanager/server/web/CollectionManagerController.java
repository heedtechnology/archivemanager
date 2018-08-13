package org.archivemanager.server.web;

import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.util.RepositoryModelEntityBinder;
import org.heed.openapps.Role;
import org.heed.openapps.User;
import org.archivemanager.RepositoryModel;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.data.FileImportProcessor;
import org.heed.openapps.security.SecurityService;
import org.heed.openapps.util.WebUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/manager")
public class CollectionManagerController {
	@Autowired private SecurityService securityService;
	@Autowired private EntityService entityService;
	@Autowired private RepositoryModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));			
		}
		return "manager/home";
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/collection", method = RequestMethod.GET)
	public String getCollection(@RequestParam("id") long id, final Model model, HttpServletRequest request, HttpServletResponse resp) throws Exception {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));
			Entity entity = entityService.getEntity(id);
			model.addAttribute("collection", binder.getResult(entity, true));
			List<FileImportProcessor> processors = (List)entityService.getImportProcessors(RepositoryModel.COLLECTION.toString());
			model.addAttribute("processors", processors);
		}
		return "manager/collection";
	}
	
	@RequestMapping(value = "/collection_import", method = RequestMethod.GET)
	public String getCollectionImport(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));			
		}
		return "manager/collection_import";
	}
	@RequestMapping(value = "/named_entities", method = RequestMethod.GET)
	public String getNamedEntities(final Model model, HttpServletRequest request, HttpServletResponse resp) {
		User user = securityService.getCurrentUser(WebUtility.getHttpRequest(request));
		if(user != null) {
			model.addAttribute("openapps_user", user);
			model.addAttribute("roles", getRolesString(user.getRoles()));
			
		}
		return "manager/named_entities";
	}
	protected String getRolesString(List<Role> roles) {
		StringWriter writer = new StringWriter();
		writer.append("[");
		for(int i=0; i < roles.size(); i++) {
			writer.append("'"+roles.get(i).getName()+"'");
			if(i < roles.size() - 1) writer.append(",");
		}
		writer.append("]");
		return writer.toString().trim();
	}
}
