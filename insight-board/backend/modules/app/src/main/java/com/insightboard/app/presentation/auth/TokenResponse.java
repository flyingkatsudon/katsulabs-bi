package com.insightboard.app.presentation.auth;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {}
