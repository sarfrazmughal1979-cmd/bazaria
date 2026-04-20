package com.platform.cms.api;

import com.platform.cms.application.dto.BannerResponse;
import com.platform.cms.application.service.BannerService;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Public Banners")
public class PublicBannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get all active banners")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners() {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getActiveBanners()));
    }

    @GetMapping("/position/{position}")
    @Operation(summary = "Get banners by position")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getBannersByPosition(
            @PathVariable String position) {
        return ResponseEntity.ok(ApiResponse.success(bannerService.getBannersByPosition(position)));
    }

    @PostMapping("/{bannerId}/click")
    @Operation(summary = "Track banner click")
    public ResponseEntity<ApiResponse<Void>> trackClick(@PathVariable UUID bannerId) {
        bannerService.trackClick(bannerId);
        return ResponseEntity.ok(ApiResponse.success("Tracked"));
    }
}