package org.cboard.controller;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.InputStream;
import java.util.Properties;

/**
 * Skips Shinhan report controllers when local dev auth is enabled ({@code dev.auth.enabled=true}
 * in {@code config.properties}, set by the {@code -Denv=local} resource overlay).
 */
public class NotLocalDevCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try (InputStream in = context.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                return true;
            }
            Properties props = new Properties();
            props.load(in);
            return !"true".equalsIgnoreCase(props.getProperty("dev.auth.enabled", "").trim());
        } catch (Exception e) {
            return true;
        }
    }
}
