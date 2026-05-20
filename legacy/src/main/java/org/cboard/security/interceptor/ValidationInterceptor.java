package org.cboard.security.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cboard.dto.User;
import org.cboard.services.SessionManageService;
import org.cboard.util.GetClientIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ValidationInterceptor implements HandlerInterceptor {

	@Autowired
	private SessionManageService sessionManageService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// userList 가져옴
		ArrayList<Object> userList = sessionManageService.getUserList();
		
		// 인증정보
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User curUser = new User();
		
		// 인증정보가 없으면 return false;
		if (auth == null) {
			request.setAttribute("msg", "로그인 정보가 존재하지 않습니다");
			response.sendRedirect("/bdp");
			return false;
		} else if (auth.getPrincipal().equals("anonymousUser")) {
			response.sendRedirect("/bdp");
			return false;
		}
		
		curUser = (User) auth.getPrincipal();
		
		String sessionId = request.getSession().getId();
		boolean isExist = false;
		
		String loginIp = GetClientIP.getClientIpAddr(request);
		
		if (userList != null) {
			// 없으면 add
			for(int i=0; i<userList.size(); i++) {	

				// map: {sessionId: ???, user: ???}, {sessionId: ???, user: ???} ....
				Map<String, Object> map = (Map<String, Object>) userList.get(i);
				
				String sId = String.valueOf(map.get("sessionId"));
				User user = (User) map.get("user");
				
				// 1. 다른 세션으로 동일한 아이디로 로그인을 했다면 (중복 로그인 시도)
				//if(!sId.equals(sessionId) && user.getUserId().equals(curUser.getUserId())) {
				if(user.getUserId().equals(curUser.getUserId())) {
					// 1-1. 중복 로그인 시도한 세션은 저장
					user.setLoginIp(loginIp);
					
					map.put("sessionId", sessionId);
					map.put("user", user);
					
					userList.add(map);
					
					// 1-2. 기존에 로그인 된 세션정보 삭제
					userList.remove(i);
					isExist = true;
					
					break;
				}
			}
			
			if(!isExist) {
				Map<String, Object> curUserMap = new HashMap<String, Object>();
				
				curUserMap.put("sessionId", sessionId);
				curUserMap.put("user", curUser);
				curUserMap.put("loginIp", loginIp);
				
				userList.add(curUserMap);
			}
			
			sessionManageService.setUserList(userList);
		} 
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// TODO Auto-generated method stub
		// logger.info("{}", request.getRequestURL());
		// HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// TODO Auto-generated method stub
		// logger.info("{}", "afterCompletion");
		// HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

	}
}