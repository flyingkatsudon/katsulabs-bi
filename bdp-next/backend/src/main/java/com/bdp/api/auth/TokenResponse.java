package com.bdp.api.auth;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {}
