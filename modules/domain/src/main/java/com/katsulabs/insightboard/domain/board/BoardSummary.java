package com.katsulabs.insightboard.domain.board;

import java.time.Instant;

public record BoardSummary(
        long id,
        String name,
        String userId,
        String userName,
        Long categoryId,
        String categoryName,
        boolean publishedToViewers,
        Instant createdAt,
        Instant updatedAt) {
}
