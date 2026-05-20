package com.bdp.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bdp.jwt")
public record JwtProperties(long accessExpirationMs, long refreshExpirationMs, String secret) {}
