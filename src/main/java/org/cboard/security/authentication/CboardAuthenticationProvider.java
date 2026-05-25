package org.cboard.security.authentication;

import org.cboard.dto.User;
import org.cboard.services.CboardUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CboardAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(CboardAuthenticationProvider.class);

	@Autowired
	private CboardUserService userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication == null) {
			throw new AuthenticationServiceException("올바르지 않은 접근입니다.");
		}
		User user = (User) authentication.getPrincipal();
		if (user == null) {
			throw new AuthenticationServiceException("올바르지 않은 접근입니다.");
		}

		User load = userService.loadUserByUsername(user);
		String retnMsg = "사번 또는 비밀번호가 일치하지 않습니다.";

		if (load == null) {
			logger.info("{}", retnMsg);
			throw new UsernameNotFoundException(retnMsg);
		}

		if (load.getDelCd().toUpperCase().equals("X")) {
			logger.info("삭제된 사번입니다");
			throw new AuthenticationServiceException(retnMsg);
		}

		if (load.getUserStateInfo().equals("1") || load.getRbacPolicy() >= 5) {
			load.setRbacPolicy(5);
			load.setUserStateInfo("1");
			userService.updateLoginCnt(load);
			throw new AuthenticationServiceException(
					"비밀번호가 5회이상 일치하지 않았습니다. 로그인이 차단됩니다. 시스템 관리자에게 요청해주세요.");
		}

		if (user.getUserPassword().equals(load.getUserPassword().toUpperCase())
				&& user.getUserId().equals(load.getUserId())
				&& user.getBusinessCode().equals(load.getBusinessCode())) {

			user.setRbacPolicy(0);
			user.setUserStateInfo("0");
			userService.updateLoginCnt(user);
			load.setV3(user.getV3());
			return new CboardAuthenticationToken(load, load.getAuthorities());
		}

		int failCnt = load.getRbacPolicy() + 1;
		load.setRbacPolicy(failCnt);
		userService.updateLoginCnt(load);
		load.setUserStateInfo(failCnt >= 5 ? "1" : "0");
		if (failCnt < 5) {
			logger.info("비밀번호가 {}회 일치하지 않았습니다.", failCnt);
		}
		throw new BadCredentialsException(retnMsg);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CboardAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
