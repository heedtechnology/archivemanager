package org.archivemanager.server.web;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.User;
import org.heed.openapps.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/")
public class MainController {
	@Autowired
    private SecurityService securityService;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getHome(HttpServletRequest request, HttpServletResponse resp) {
		return "home";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }
	@RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User user, BindingResult bindingResult, Model model) {
    	ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
        	bindingResult.rejectValue("username", "Size.userForm.username");
        }
        if(securityService.getUserByUsername(user.getUsername()) != null) {
        	bindingResult.rejectValue("username", "Duplicate.userForm.username");
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
        	bindingResult.rejectValue("password", "Size.userForm.password");
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        securityService.addUser(user);

        //securityService.autologin(user.getUsername(), user.getPassword());

        return "redirect:/";
    }
}
