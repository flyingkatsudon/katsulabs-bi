package org.cboard.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cboard.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SessionManageService {
	
	private static ArrayList<Object> userList = new ArrayList<Object>();
	private static final Logger logger = LoggerFactory.getLogger(SessionManageService.class);
	
	public ArrayList<Object> getUserList() {
		return userList;
	}
	
	public void setUserList(ArrayList<Object> userList) {
		this.userList = userList;
	}
	
	public Map<String, Object> checkSn(HttpServletRequest request) {
		
		String sessionId = request.getSession().getId();
		User curUser = (User) request.getSession().getAttribute("user");
		String userId = "";

		if (curUser != null) userId = curUser.getUserId();
		else return null;
		
		Map<String, Object> retnMap   = new HashMap<String, Object>();	
		
		boolean status = true;
		
		if (userList.size() == 0) {
			status = true;
		} else {
			for(Object o : userList) {
				Map<String, Object> map = (Map<String, Object>) o;
				User user = (User) map.get("user");

				if (!map.get("sessionId").equals(sessionId) && user.getUserId().equals(userId)) {
					status = false;
					retnMap.put("loginIp", user.getLoginIp());
					break;
				}
			}
		}

		retnMap.put("status", status);
		return retnMap;
	}
	
	public Map<String, Object> getUserId(HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) request.getSession().getAttribute("user");
		
		map.put("userId", user.getUserId());
		
		return map;
	}
}
