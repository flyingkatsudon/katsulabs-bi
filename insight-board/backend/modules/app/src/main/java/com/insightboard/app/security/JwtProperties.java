package com.insightboard.app.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "insightboard.jwt")
public record JwtProperties(long accessExpirationMs, long refreshExpirationMs, String secret) {}
