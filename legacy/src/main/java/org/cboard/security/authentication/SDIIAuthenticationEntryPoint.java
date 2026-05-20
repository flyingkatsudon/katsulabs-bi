package org.cboard.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class SDIIAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(SDIIAuthenticationEntryPoint.class);
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		logger.info("SDIIAuthenticationEntryPoint: {}", request);
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}
