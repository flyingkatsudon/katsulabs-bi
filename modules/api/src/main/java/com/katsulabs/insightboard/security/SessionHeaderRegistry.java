package com.katsulabs.insightboard.security;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.katsulabs.insightboard.application.auth.AuthenticatedUser;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Vite dev: 브라우저가 JSESSIONID 쿠키를 저장하지 못할 때 {@link #SESSION_HEADER} 로 인증.
 */
@Component
public class SessionHeaderRegistry {

    public static final String SESSION_HEADER = "X-Insightboard-Session";

    private final Map<String, AuthenticatedUser> bySessionId = new ConcurrentHashMap<>();

    public void bind(String sessionId, AuthenticatedUser user) {
        if (sessionId != null && !sessionId.isBlank() && user != null) {
            bySessionId.put(sessionId, user);
        }
    }

    public void unbind(String sessionId) {
        if (sessionId != null) {
            bySessionId.remove(sessionId);
        }
    }

    public Optional<AuthenticatedUser> lookup(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(bySessionId.get(sessionId.trim()));
    }

    @PreDestroy
    void clear() {
        bySessionId.clear();
    }
}
