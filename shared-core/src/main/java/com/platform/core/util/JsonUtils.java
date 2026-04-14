package com.platform.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private JsonUtils() {}

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    public static <T> Optional<T> fromJson(String json, Class<T> clazz) {
        try {
            return Optional.of(MAPPER.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize JSON to {}", clazz.getSimpleName(), e);
            return Optional.empty();
        }
    }

    public static String toPrettyJson(Object object) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to pretty JSON", e);
            return "{}";
        }
    }
}