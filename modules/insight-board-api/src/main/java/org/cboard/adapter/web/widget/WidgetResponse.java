package org.cboard.adapter.web.widget;

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
