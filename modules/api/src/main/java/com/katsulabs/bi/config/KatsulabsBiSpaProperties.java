package com.katsulabs.bi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "katsulabs.bi.spa")
public record KatsulabsBiSpaProperties(boolean enabled) {

    public KatsulabsBiSpaProperties() {
        this(false);
    }
}
