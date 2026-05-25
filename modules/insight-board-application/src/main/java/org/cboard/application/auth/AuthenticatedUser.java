package org.cboard.application.auth;

import java.util.List;

public record AuthenticatedUser(
        String userId,
        String businessCode,
        String loginName,
        String displayName,
        String roleId,
        List<Long> resourceTypes) {
}
