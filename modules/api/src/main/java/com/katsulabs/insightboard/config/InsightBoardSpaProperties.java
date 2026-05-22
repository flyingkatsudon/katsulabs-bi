package com.katsulabs.insightboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "insightboard.spa")
public record InsightBoardSpaProperties(boolean enabled) {

    public InsightBoardSpaProperties() {
        this(false);
    }
}
