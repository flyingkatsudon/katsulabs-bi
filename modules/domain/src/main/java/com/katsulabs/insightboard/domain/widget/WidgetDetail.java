package com.katsulabs.insightboard.domain.widget;

import java.time.Instant;

public record WidgetDetail(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        String dataJson,
        Instant createdAt,
        Instant updatedAt) {
}
