package org.cboard.adapter.web.auth;

import java.util.List;

import org.cboard.application.auth.AuthenticatedUser;
import org.cboard.application.auth.LoginCommand;
import org.cboard.application.auth.LoginUseCase;
import org.cboard.config.CboardProperties;
import org.cboard.security.CboardUserPrincipal;
import org.cboard.security.SessionAuthenticationFilter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final CboardProperties cboardProperties;

    public AuthController(LoginUseCase loginUseCase, CboardProperties cboardProperties) {
        this.loginUseCase = loginUseCase;
        this.cboardProperties = cboardProperties;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String businessCode = request.businessCode() == null || request.businessCode().isBlank()
                ? cboardProperties.getDefaultBusinessCode()
                : request.businessCode();

        AuthenticatedUser user = loginUseCase.login(
                new LoginCommand(businessCode, request.userId(), request.password()));

        var principal = new CboardUserPrincipal(user);
        var authorities = user.roleId() == null
                ? List.<SimpleGrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.roleId()));
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true).setAttribute(SessionAuthenticationFilter.SESSION_USER_KEY, user);

        return new LoginResponse(
                user.userId(),
                user.businessCode(),
                user.loginName(),
                user.displayName(),
                user.roleId(),
                user.resourceTypes());
    }
}
