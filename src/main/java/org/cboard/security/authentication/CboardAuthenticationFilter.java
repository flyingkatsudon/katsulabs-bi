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

public class CboardAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger logger = LoggerFactory.getLogger(CboardAuthenticationFilter.class);

	public static final String SPRING_SECURITY_FORM_V0_KEY = "v0";
	public static final String SPRING_SECURITY_FORM_V1_KEY = "v1";
	public static final String SPRING_SECURITY_FORM_V2_KEY = "v2";

	private String v0Parameter = SPRING_SECURITY_FORM_V0_KEY;
	private String v1Parameter = SPRING_SECURITY_FORM_V1_KEY;
	private String v2Parameter = SPRING_SECURITY_FORM_V2_KEY;

	private boolean postOnly = true;

	private final LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler();
	private final LoginFailureHandler loginFailureHandler = new LoginFailureHandler();

	@Value("${initPwd}")
	private String initPwd;

	public CboardAuthenticationFilter() {
		super(new AntPathRequestMatcher("/process", "POST"));
	}

	public String validation(String str) {
		if (str == null || str.isEmpty()) {
			throw new AuthenticationServiceException("사번을 입력해주세요");
		}
		if (str.length() > 10 || str.length() < 6) {
			throw new AuthenticationServiceException("사번은 6자리에서 10자리 사이여야 합니다");
		}
		if (str.contains(" ")) {
			throw new AuthenticationServiceException("사번은 공백을 포함할 수 없습니다");
		}
		return str;
	}

	public String validationPw(String str) {
		if (str == null || str.isEmpty()) {
			throw new AuthenticationServiceException("비밀번호를 입력해주세요");
		}
		return str;
	}

	@Override
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

		CboardAuthenticationToken authRequest = new CboardAuthenticationToken(user);
		setDetails(request, authRequest);
		return getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		super.doFilter(req, res, chain);
	}

	@Override
	public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
		logger.debug("login success handler configured");
		super.setAuthenticationSuccessHandler(successHandler);
	}

	@Override
	public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
		logger.debug("login failure handler configured");
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

	protected void setDetails(HttpServletRequest request, CboardAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		loginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		loginFailureHandler.onAuthenticationFailure(request, response, failed);
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}
}
