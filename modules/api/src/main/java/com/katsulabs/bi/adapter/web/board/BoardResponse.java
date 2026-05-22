package com.katsulabs.bi.adapter.web.board;

import java.time.Instant;

public record BoardResponse(
        long id,
        String name,
        String userId,
        String userName,
        Long categoryId,
        String categoryName,
        String layoutJson,
        boolean publishedToViewers,
        Instant createdAt,
        Instant updatedAt) {
}
