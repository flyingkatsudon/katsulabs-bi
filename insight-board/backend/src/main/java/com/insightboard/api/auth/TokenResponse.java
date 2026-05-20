package com.insightboard.api.auth;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {}
