package com.platform.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorInfo error;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorInfo(errorCode, message, null))
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, Object details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorInfo(errorCode, message, details))
                .timestamp(Instant.now())
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorInfo {
        private String code;
        private String message;
        private Object details;
    }
}