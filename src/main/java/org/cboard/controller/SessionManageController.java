package org.cboard.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cboard.services.SessionManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sm")
public class SessionManageController {
	
	@Autowired
	private SessionManageService sessionManageService;
	
	@RequestMapping(value = "/checksn", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkSn(HttpServletRequest request) {
		return sessionManageService.checkSn(request);	
	}
	
	@RequestMapping(value = "/get_userid", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getUserId(HttpServletRequest request) {
		return sessionManageService.getUserId(request);	
	}
}
