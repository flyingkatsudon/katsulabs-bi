package com.katsulabs.bi.adapter.web.domains.auth;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.application.domains.auth.AuthenticatedUser;
import com.katsulabs.bi.application.domains.auth.LoginCommand;
import com.katsulabs.bi.application.domains.auth.LoginUseCase;
import com.katsulabs.bi.security.KatsulabsBiUserPrincipal;
import com.katsulabs.bi.security.SessionAuthenticationFilter;
import com.katsulabs.bi.security.SessionHeaderRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final SessionHeaderRegistry sessionHeaderRegistry;


    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthenticatedUser user =
                loginUseCase.login(new LoginCommand(request.userId(), request.password()));

        var principal = new KatsulabsBiUserPrincipal(user);
        var authorities = user.roleId() == null
                ? List.<SimpleGrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.roleId()));
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var httpSession = httpRequest.getSession(true);
        SessionAuthenticationFilter.storeSessionUser(httpSession, user);
        sessionHeaderRegistry.bind(httpSession.getId(), user);

        return toLoginResponse(user, httpSession.getId());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest httpRequest) {
        String headerId = httpRequest.getHeader(SessionHeaderRegistry.SESSION_HEADER);
        if (headerId != null && !headerId.isBlank()) {
            sessionHeaderRegistry.unbind(headerId);
        }
        var session = httpRequest.getSession(false);
        if (session != null) {
            sessionHeaderRegistry.unbind(session.getId());
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @GetMapping("/session")
    public ResponseEntity<LoginResponse> session(HttpServletRequest httpRequest) {
        String headerId = httpRequest.getHeader(SessionHeaderRegistry.SESSION_HEADER);
        if (headerId != null && !headerId.isBlank()) {
            return sessionHeaderRegistry
                    .lookup(headerId)
                    .map(user -> ResponseEntity.ok(toLoginResponse(user, headerId.trim())))
                    .orElseGet(() -> ResponseEntity.noContent().build());
        }
        var httpSession = httpRequest.getSession(false);
        if (httpSession == null) {
            return ResponseEntity.noContent().build();
        }
        AuthenticatedUser user = SessionAuthenticationFilter.getSessionUser(httpSession);
        if (user == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(toLoginResponse(user, httpSession.getId()));
    }

    private static LoginResponse toLoginResponse(AuthenticatedUser user, String sessionId) {
        return new LoginResponse(
                user.userId(),
                user.loginName(),
                user.displayName(),
                user.roleId(),
                user.roleName(),
                user.defaultBoardId(),
                user.resourceTypes(),
                sessionId);
    }
}
