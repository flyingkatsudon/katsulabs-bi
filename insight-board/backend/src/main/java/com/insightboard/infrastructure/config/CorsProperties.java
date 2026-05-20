package com.insightboard.infrastructure.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "insightboard.cors")
public record CorsProperties(List<String> allowedOrigins) {}
