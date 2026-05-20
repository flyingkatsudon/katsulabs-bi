package com.bdp.infrastructure.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bdp.cors")
public record CorsProperties(List<String> allowedOrigins) {}
