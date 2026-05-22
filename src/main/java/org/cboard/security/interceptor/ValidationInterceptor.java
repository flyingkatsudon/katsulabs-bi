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