package com.katsulabs.insightboard.adapter.web.board;

import java.time.Instant;

public record BoardResponse(
        long id,
        String name,
        String userId,
        String userName,
        Long categoryId,
        String categoryName,
        String layoutJson,
        Instant createdAt,
        Instant updatedAt) {
}
