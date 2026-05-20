package com.insightboard.api.board;

public record BoardResponse(Long boardId, String userId, Long categoryId, String boardName, String layoutJson) {}
