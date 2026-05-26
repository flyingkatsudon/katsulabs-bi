package com.katsulabs.bi.security;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import com.katsulabs.bi.application.domains.auth.AuthenticatedUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    public static final String SESSION_USER_KEY = "KATSULABS_BI_USER";

    /** 구 세션 키 — 읽기 호환만 유지 (값은 레거시 WAR와 동일) */
    private static final String LEGACY_SESSION_USER_KEY = "CBoard_USER";

    public static void storeSessionUser(HttpSession session, AuthenticatedUser user) {
        session.setAttribute(SESSION_USER_KEY, user);
        session.removeAttribute(LEGACY_SESSION_USER_KEY);
    }

    private final SessionHeaderRegistry sessionHeaderRegistry;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (shouldRestoreFromSession(current)) {
            AuthenticatedUser user = resolveUser(request);
            if (user != null) {
                setAuthentication(user);
            }
        }

        filterChain.doFilter(request, response);
    }

    private AuthenticatedUser resolveUser(HttpServletRequest request) {
        String headerId = request.getHeader(SessionHeaderRegistry.SESSION_HEADER);
        if (headerId != null && !headerId.isBlank()) {
            var fromHeader = sessionHeaderRegistry.lookup(headerId);
            if (fromHeader.isPresent()) {
                return fromHeader.get();
            }
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return getSessionUser(session);
    }

    /** HTTP 세션에 저장된 로그인 사용자 (신규·레거시 키 모두 조회). */
    public static AuthenticatedUser getSessionUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object stored = session.getAttribute(SESSION_USER_KEY);
        if (stored instanceof AuthenticatedUser user) {
            return user;
        }
        stored = session.getAttribute(LEGACY_SESSION_USER_KEY);
        if (stored instanceof AuthenticatedUser user) {
            return user;
        }
        return null;
    }

    private static void setAuthentication(AuthenticatedUser user) {
        var principal = new KatsulabsBiUserPrincipal(user);
        var authorities = user.roleId() == null
                ? List.<SimpleGrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.roleId()));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private static boolean shouldRestoreFromSession(Authentication current) {
        if (current == null) {
            return true;
        }
        if (current instanceof AnonymousAuthenticationToken) {
            return true;
        }
        if (current.getPrincipal() instanceof KatsulabsBiUserPrincipal) {
            return false;
        }
        return !current.isAuthenticated();
    }
}
