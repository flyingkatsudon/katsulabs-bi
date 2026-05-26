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

    public static final String SESSION_USER_KEY = "CBoard_USER";

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
        Object stored = session.getAttribute(SESSION_USER_KEY);
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
