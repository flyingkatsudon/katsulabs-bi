package com.insightboard.api.presentation.board;

public record BoardResponse(Long boardId, String userId, Long categoryId, String boardName, String layoutJson) {}
