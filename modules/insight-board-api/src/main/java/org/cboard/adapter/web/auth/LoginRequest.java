package org.cboard.adapter.web.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        String businessCode,
        @NotBlank String userId,
        @NotBlank String password) {
}
