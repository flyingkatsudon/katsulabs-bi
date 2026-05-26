package com.katsulabs.bi.adapter.web.domains.widget;

import java.time.Instant;

public record WidgetResponse(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        String dataJson,
        Instant createdAt,
        Instant updatedAt) {
}
