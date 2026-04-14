package com.platform.cms.api;

import com.platform.cms.application.dto.PageResponse;
import com.platform.cms.application.service.PageService;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/pages")
@RequiredArgsConstructor
public class PublicPageController {

    private final PageService pageService;

    @GetMapping("/{slug}")
    @Operation(summary = "Get page by slug")
    public ResponseEntity<ApiResponse<PageResponse>> getPage(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(pageService.getPageBySlug(slug)));
    }
}