package com.bdp.api.board;

import jakarta.validation.constraints.NotBlank;

public record BoardRequest(@NotBlank String boardName, Long categoryId, String layoutJson) {}
