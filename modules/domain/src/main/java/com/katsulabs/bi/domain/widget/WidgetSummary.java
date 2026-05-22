package com.katsulabs.bi.domain.widget;

import java.time.Instant;

public record WidgetSummary(
        long id,
        String name,
        String userId,
        String userName,
        String categoryName,
        Instant createdAt,
        Instant updatedAt) {
}
