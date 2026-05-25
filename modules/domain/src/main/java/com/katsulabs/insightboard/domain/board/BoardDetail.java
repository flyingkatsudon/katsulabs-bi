package com.katsulabs.insightboard.domain.board;

import java.time.Instant;

public record BoardDetail(
        long id,
        String name,
        String userId,
        String userName,
        Long categoryId,
        String layoutJson,
        Instant createdAt,
        Instant updatedAt) {
}
