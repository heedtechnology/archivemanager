package org.archivemanager.server.web;

import java.util.logging.Logger;

import org.heed.openapps.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reserve")
public class DigitalReserveController {
	private final static Logger log = Logger.getLogger(DigitalReserveController.class.getName());
	@Autowired private SearchService searchService;
	
	
	@GetMapping("")
	public String getDefault(Model model) {
		
		return "reserve/viewer";
	}
	@GetMapping("/html")
	public String getDefaultHtml(Model model) {
		
		return "reserve/viewer-html";
	}
}
