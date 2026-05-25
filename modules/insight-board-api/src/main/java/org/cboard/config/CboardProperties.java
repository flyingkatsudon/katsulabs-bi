package org.cboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cboard")
public class CboardProperties {

    private String defaultBusinessCode = "SH";

    public String getDefaultBusinessCode() {
        return defaultBusinessCode;
    }

    public void setDefaultBusinessCode(String defaultBusinessCode) {
        this.defaultBusinessCode = defaultBusinessCode;
    }
}
