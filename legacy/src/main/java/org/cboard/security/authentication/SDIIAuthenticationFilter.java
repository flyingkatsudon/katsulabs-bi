package org.cboard.security.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cboard.dto.User;
import org.cboard.security.handler.LoginFailureHandler;
import org.cboard.security.handler.LoginSuccessHandler;
import org.cboard.util.CheckPwd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

public class SDIIAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger logger = LoggerFactory.getLogger(SDIIAuthenticationFilter.class);

	public static final String SPRING_SECURITY_FORM_V0_KEY = "v0";
	public static final String SPRING_SECURITY_FORM_V1_KEY = "v1";
	public static final String SPRING_SECURITY_FORM_V2_KEY = "v2";

	private String v0Parameter = SPRING_SECURITY_FORM_V0_KEY;
	private String v1Parameter = SPRING_SECURITY_FORM_V1_KEY;
	private String v2Parameter = SPRING_SECURITY_FORM_V2_KEY;

	private boolean postOnly = true;

	private LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler();
	private LoginFailureHandler loginFailureHandler = new LoginFailureHandler();

	@Value("${initPwd}")
	private String initPwd;
	
	public SDIIAuthenticationFilter() {
		super(new AntPathRequestMatcher("/process", "POST"));
	}

	// v0 ~ v4 유효성 검사
	public String validation(String str) {
		
		if (str == null || str.isEmpty()) throw new AuthenticationServiceException("사번을 입력해주세요");
		else if ((str.length() > 10 || str.length() < 6)) throw new AuthenticationServiceException("사번은 6자리에서 10자리 사이여야 합니다"); 
		else if (str.contains(" ")) throw new AuthenticationServiceException("사번은 공백을 포함할 수 없습니다");
		
		return str;
	}
	
	public String validationPw(String str) {
		
		if (str == null || str.isEmpty()) throw new AuthenticationServiceException("비밀번호를 입력해주세요");
		
		return str;
	}
	
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String v0 = obtainV0(request).trim();
		String v1 = validation(obtainV1(request));
		String v2 = validationPw(obtainV2(request));

		User user = new User();
		user.setV0(v0);
		user.setV1(v1);
		user.setV2(v2);
		user.setV3(initPwd);

		user.setBusinessCode(v0);
		user.setUserId(v1);
		user.setUserPassword(CheckPwd.passwordEncoder.encodePassword(v2, "").toUpperCase());
        
		SDIIAuthenticationToken authRequest = new SDIIAuthenticationToken(user);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		super.doFilter(req, res, chain);
	}

	@Override
	public void setFilterProcessesUrl(String filterProcessesUrl) {
		// TODO Auto-generated method stub
		super.setFilterProcessesUrl(filterProcessesUrl);
	}

	@Override
	public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
		// TODO Auto-generated method stub
		logger.info("{}", "login success");
		super.setAuthenticationSuccessHandler(successHandler);
	}

	@Override
	public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
		// TODO Auto-generated method stub
		logger.info("{}", "login failure");
		super.setAuthenticationFailureHandler(failureHandler);
	}

	protected String obtainV0(HttpServletRequest request) {
		return request.getParameter(v0Parameter);
	}

	protected String obtainV1(HttpServletRequest request) {
		return request.getParameter(v1Parameter);
	}

	protected String obtainV2(HttpServletRequest request) {
		return request.getParameter(v2Parameter);
	}

	protected void setDetails(HttpServletRequest request, SDIIAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	public void setV0Parameter(String v0) {
		Assert.hasText(v0, "v0 parameter must not be empty or null");
		this.v0Parameter = v0;
	}

	public void setV1Parameter(String v1) {
		Assert.hasText(v1, "v1 parameter must not be empty or null");
		this.v1Parameter = v1;
	}

	public void setV2Parameter(String v2) {
		Assert.hasText(v2, "v2 parameter must not be empty or null");
		this.v2Parameter = v2;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		loginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		loginFailureHandler.onAuthenticationFailure(request, response, failed);
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public final String getV0() {
		return v0Parameter;
	}

	public final String getV1() {
		return v1Parameter;
	}

	public final String getV2() {
		return v2Parameter;
	}
}
