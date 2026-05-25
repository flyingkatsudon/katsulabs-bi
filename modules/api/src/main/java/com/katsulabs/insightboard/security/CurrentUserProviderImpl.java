package com.katsulabs.insightboard.security;

import com.katsulabs.insightboard.application.common.CurrentUserProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProviderImpl implements CurrentUserProvider {

    @Override
    public String requireUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof InsightBoardUserPrincipal principal) {
            return principal.user().userId();
        }
        throw new IllegalStateException("로그인이 필요합니다.");
    }
}
