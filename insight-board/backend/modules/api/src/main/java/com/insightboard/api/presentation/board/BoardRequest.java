package com.insightboard.api.presentation.board;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(@NotBlank String boardName, Long categoryId, String layoutJson) {}
