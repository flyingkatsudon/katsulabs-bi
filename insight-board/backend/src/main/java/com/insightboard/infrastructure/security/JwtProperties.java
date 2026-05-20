package com.insightboard.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "insightboard.jwt")
public record JwtProperties(long accessExpirationMs, long refreshExpirationMs, String secret) {}
