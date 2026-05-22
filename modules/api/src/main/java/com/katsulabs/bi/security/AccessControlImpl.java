package com.katsulabs.bi.security;

import com.katsulabs.bi.application.auth.KatsulabsBiRole;
import com.katsulabs.bi.application.common.AccessControl;
import com.katsulabs.bi.application.common.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccessControlImpl implements AccessControl {

    @Override
    public KatsulabsBiRole requireRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof KatsulabsBiUserPrincipal principal) {
            return KatsulabsBiRole.fromRoleId(principal.user().roleId());
        }
        throw new AccessDeniedException("로그인이 필요합니다.");
    }

    @Override
    public void requireManageUsers() {
        if (!requireRole().canManageUsers()) {
            throw new AccessDeniedException("사용자 관리 권한이 없습니다.");
        }
    }

    @Override
    public void requireWriteDatasourceOrDataset() {
        if (!requireRole().canWriteDatasourceOrDataset()) {
            throw new AccessDeniedException("데이터소스·데이터셋 변경 권한이 없습니다.");
        }
    }

    @Override
    public void requireWriteDashboardContent() {
        if (!requireRole().canWriteDashboardContent()) {
            throw new AccessDeniedException("대시보드·위젯·카테고리 변경 권한이 없습니다.");
        }
    }
}
