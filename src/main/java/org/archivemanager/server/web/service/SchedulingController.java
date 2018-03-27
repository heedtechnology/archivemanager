package org.archivemanager.server.web.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.data.RestResponse;
import org.heed.openapps.scheduling.Job;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/scheduling")
public class SchedulingController extends WebserviceSupport {

	
	@ResponseBody
	@RequestMapping(value="/status.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchEntity(@RequestParam(required=true) String uid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		prepareResponse(response);		
		RestResponse<Object> data = new RestResponse<Object>();
		try {
			Job job = getSchedulingService().getJob(uid);
			if(job != null) {
				Map<String,Object> statusData = new HashMap<String,Object>();
				statusData.put("uid", job.getUid());
				statusData.put("lastMessage", job.getLastMessage());
				statusData.put("isRunning", !job.isComplete());
				data.getResponse().addData(statusData);
			}
			data.getResponse().setStatus(0);
		} catch(Exception e) {
			e.printStackTrace();
			data.getResponse().setStatus(-1);
			data.getResponse().addMessage(e.getMessage());
		}
		return data;
	}
	
}
