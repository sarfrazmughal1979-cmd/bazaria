package com.platform.core.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super("RESOURCE_NOT_FOUND",
                String.format("%s not found with %s: '%s'", resource, field, value));
    }

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
}