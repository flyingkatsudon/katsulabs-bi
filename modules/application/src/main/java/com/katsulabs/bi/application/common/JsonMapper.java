package com.katsulabs.bi.application.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson 기반 JSON 유틸. Fastjson/org.json 대체용 단일 진입점.
 */
public final class JsonMapper {

    private static final ObjectMapper MAPPER = createMapper();

    private JsonMapper() {
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 직렬화 실패", e);
        }
    }

    public static byte[] toBytes(Object value) {
        try {
            return MAPPER.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 직렬화 실패", e);
        }
    }

    public static void writeValue(OutputStream out, Object value) {
        try {
            MAPPER.writeValue(out, value);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON 출력 실패", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 파싱 실패", e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 파싱 실패", e);
        }
    }

    public static <T> T fromStream(InputStream in, Class<T> type) {
        try {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON 파싱 실패", e);
        }
    }

    public static <T> Optional<T> fromJsonOptional(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(fromJson(json, type));
    }
}
