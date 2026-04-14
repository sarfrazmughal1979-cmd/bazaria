package com.platform.core.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends BusinessException {

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super("VALIDATION_ERROR", "Validation failed");
        this.errors = errors;
    }

    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", message);
        this.errors = Map.of(field, message);
    }
}