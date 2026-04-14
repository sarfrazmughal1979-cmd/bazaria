package com.platform.cms.api.admin;

import com.platform.cms.application.dto.BannerRequest;
import com.platform.cms.application.dto.BannerResponse;
import com.platform.cms.application.service.BannerService;
import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/banners")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @PostMapping
    @Operation(summary = "Create a new banner")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @Valid @RequestBody BannerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.createBanner(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a banner")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable UUID id,
            @Valid @RequestBody BannerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.updateBanner(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a banner")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable UUID id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success("Banner deleted"));
    }

    @GetMapping
    @Operation(summary = "Get all banners (admin)")
    public ResponseEntity<ApiResponse<PagedResponse<BannerResponse>>> getAllBanners(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getAllBanners(pageable)));
    }
}