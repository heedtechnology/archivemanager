package org.archivemanager.server.web;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.data.ActionMessage;
import org.heed.openapps.events.ActionMessageListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/service/messaging*")
public class MessagingController {
	private static final Logger log = Logger.getLogger(MessagingController.class.getName());	
	private ActionMessageListener listener;
	
	
	@RequestMapping(value="/inbound", method = RequestMethod.POST,consumes = "text/plain;charset=UTF-8",produces = "text/plain;charset=UTF-8")
    public void inboundPost(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		ActionMessage message = new ActionMessage(/*response*/);
		try {
			message.fromJson(data);
			listener.onMessage(message);
		} catch(Exception e) {
			log.log(Level.SEVERE, data, e);
		}
	}

	public void setListener(ActionMessageListener listener) {
		this.listener = listener;
	}
		
}
