package org.cboard.adapter.web.auth;

import java.util.List;

public record LoginResponse(
        String userId,
        String businessCode,
        String loginName,
        String displayName,
        String roleId,
        List<Long> resourceTypes) {
}
