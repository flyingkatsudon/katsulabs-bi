package com.insightboard.api.application.cboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public final class CboardQueryParams {

    private CboardQueryParams() {}

    public static Map<String, String> parse(String queryJson, ObjectMapper mapper) {
        if (queryJson == null || queryJson.isBlank()) {
            return Map.of();
        }
        try {
            Map<String, Object> raw = mapper.readValue(queryJson, new TypeReference<>() {});
            return raw.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
        } catch (Exception e) {
            return Map.of();
        }
    }
}
