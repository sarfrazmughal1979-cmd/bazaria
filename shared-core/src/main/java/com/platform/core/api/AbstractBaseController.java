package com.platform.core.api;

import com.platform.core.dto.ApiResponse;
import com.platform.core.security.SecurityUtils;
import com.platform.core.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.Map;

/**
 * Abstract base controller for all REST controllers in the platform.
 * Provides common utilities and standardized response methods.
 */
@Slf4j
public abstract class AbstractBaseController {

    // ============================================================
    // Response builders
    // ============================================================

    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, message));
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message, errorCode));
    }

    protected <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(message, errorCode));
    }

    // ============================================================
    // Current user helpers
    // ============================================================

    protected UserContext getCurrentUser() {
        return SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));
    }

    protected boolean isAuthenticated() {
        return SecurityUtils.isAuthenticated();
    }

    // ============================================================
    // Pagination helpers
    // ============================================================

    protected Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequest.of(page, size, sort);
    }

    protected Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    // ============================================================
    // Common endpoints (can be overridden)
    // ============================================================

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "service", getServiceName()
        ));
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
                "version", getVersion(),
                "buildTime", getBuildTime(),
                "service", getServiceName()
        ));
    }

    // ============================================================
    // Abstract methods for subclasses to implement
    // ============================================================

    /**
     * Return the name of the module/service (e.g., "order-module", "catalog-module").
     */
    protected abstract String getServiceName();

    /**
     * Return the version string (can be from properties file).
     * Default implementation returns "1.0.0".
     */
    protected String getVersion() {
        return "1.0.0";
    }

    /**
     * Return build timestamp.
     */
    protected String getBuildTime() {
        return "unknown";
    }
}