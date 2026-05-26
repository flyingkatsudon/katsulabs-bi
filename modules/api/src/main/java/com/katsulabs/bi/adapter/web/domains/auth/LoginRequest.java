package com.katsulabs.bi.adapter.web.domains.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String userId, @NotBlank String password) {}
