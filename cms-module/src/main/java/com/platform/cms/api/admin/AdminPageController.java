package com.platform.cms.api.admin;

import com.platform.cms.application.dto.PageRequest;
import com.platform.cms.application.dto.PageResponse;
import com.platform.cms.application.service.PageService;
import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/pages")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminPageController {

    private final PageService pageService;

    @PostMapping
    public ResponseEntity<ApiResponse<PageResponse>> createPage(@Valid @RequestBody PageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pageService.createPage(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PageResponse>> updatePage(
            @PathVariable UUID id,
            @Valid @RequestBody PageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pageService.updatePage(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePage(@PathVariable UUID id) {
        pageService.deletePage(id);
        return ResponseEntity.ok(ApiResponse.success("Page deleted"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PageResponse>>> getAllPages(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(pageService.getPublishedPages(pageable)));
    }
}