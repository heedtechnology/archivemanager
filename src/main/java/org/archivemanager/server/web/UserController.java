package org.archivemanager.server.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.data.RestResponse;
import org.archivemanager.data.SystemModelEntityBinder;
import org.heed.openapps.Role;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.entity.InvalidEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/users")
public class UserController {
	@Autowired private EntityService entityService;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private SystemModelEntityBinder binder;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getUsers(final Model model, HttpServletRequest request, HttpServletResponse resp) {		
		List<Role> roles = getRoles();
		model.addAttribute("roles", roles);
		model.addAttribute("newUser", new User());
		return "users/users";
	}	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editUser(@RequestParam long id, final Model model) throws InvalidEntityException {
		User user = binder.getUser(entityService.getEntity(id));
		model.addAttribute("user", user);
		List<Role> roles = getRoles();
		model.addAttribute("roles", roles);
		return "users/user";
	}
	/*
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String registration(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUsername((String)auth.getPrincipal());
        if(user == null) return "login";
		model.addAttribute("user", user);

        return "apps/profile";
    }
	*/
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
		EntityResultSet results = entityService.getEntities(SystemModel.USER, page, size);
		for(Entity result : results.getData()) {
			data.addRow(binder.getUser(result));
		}
		data.setTotal(results.getSize());
		return data;
	}	
	@ResponseBody
	@RequestMapping(value="/add.json", method=RequestMethod.POST)
	public RestResponse<User> addUser(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();
		User currentUser = getByUsername(user.getUsername());
		if(currentUser == null) {
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			entityService.updateEntity(binder.getEntity(user));
			data.setStatus(200);
		} else {
			data.setStatus(400);
			data.addMessage("username exists, please try again");
		}
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value="/profile/save.json", method=RequestMethod.POST)
	public RestResponse<User> saveUserProfile(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();	
		User currentUser = binder.getUser(entityService.getEntity(user.getId()));
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		if(user.getPassword() != null && user.getPassword().length() > 0) 
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
		entityService.updateEntity(binder.getEntity(currentUser));
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/save.json", method=RequestMethod.POST)
	public RestResponse<User> saveUser(@ModelAttribute("user") User user) throws Exception {
		RestResponse<User> data = new RestResponse<User>();	
		User currentUser = binder.getUser(entityService.getEntity(user.getId()));
		if(!currentUser.getUsername().equals(user.getUsername())) {
			//username mismatch, we need to make sure the new username isn't taken
			User existingUser = getByUsername(user.getUsername());
			if(existingUser != null) {
				data.setStatus(400);
				data.addMessage("username exists, please try again");
				return data;
			}
		}
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		currentUser.setEnabled(user.isEnabled());
		if(user.getPassword() != null && user.getPassword().length() > 0) 
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
		entityService.updateEntity(binder.getEntity(currentUser));
		data.setStatus(200);
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/remove.json", method=RequestMethod.POST)
	public RestResponse<User> removeUser(@RequestParam long id) throws Exception {
		RestResponse<User> data = new RestResponse<User>();
		Entity entity = entityService.getEntity(id);
		entityService.removeEntity(entity.getQName(), entity.getId());
		return data;
	}
	
	protected List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>();
		EntityResultSet entities = entityService.getEntities(SystemModel.ROLE, 0, 0);
		for(Entity result : entities.getData()) {
			roles.add(binder.getRole(result));
		}
		return roles;
	}
	protected User getByUsername(String username) throws InvalidEntityException {
		User user = null;
		Entity entity = entityService.getEntity(SystemModel.USER, SystemModel.USERNAME, username);
		user = binder.getUser(entity);
		return user;
	}
}
