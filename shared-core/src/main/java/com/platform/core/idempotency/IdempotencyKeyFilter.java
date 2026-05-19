package com.platform.core.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class IdempotencyKeyFilter extends OncePerRequestFilter {

    private final IdempotencyService idempotencyService;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/internal/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (HttpMethod.POST.matches(request.getMethod()) || HttpMethod.PUT.matches(request.getMethod())) {
            String idempotencyKey = request.getHeader("Idempotency-Key");
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Idempotency-Key header is required for POST/PUT\"}");
                return;
            }
            if (idempotencyService.isProcessed(idempotencyKey)) {
                response.setStatus(409);
                response.getWriter().write("{\"error\":\"Duplicate request\"}");
                return;
            }
            filterChain.doFilter(request, response);
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                idempotencyService.markProcessed(idempotencyKey, Duration.ofHours(24));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
