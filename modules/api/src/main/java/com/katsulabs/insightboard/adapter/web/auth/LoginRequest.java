package com.katsulabs.insightboard.adapter.web.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String userId, @NotBlank String password) {}
