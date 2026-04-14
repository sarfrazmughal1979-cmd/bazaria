package com.platform.cms.api.admin;

import com.platform.cms.application.dto.HomepageSectionRequest;
import com.platform.cms.application.dto.HomepageSectionResponse;
import com.platform.cms.application.service.HomepageService;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/homepage")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminHomepageController {

    private final HomepageService homepageService;

    @PostMapping("/sections")
    @Operation(summary = "Create a new homepage section")
    public ResponseEntity<ApiResponse<HomepageSectionResponse>> createSection(
            @Valid @RequestBody HomepageSectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(homepageService.createSection(request)));
    }

    @GetMapping("/sections")
    @Operation(summary = "Get all active homepage sections")
    public ResponseEntity<ApiResponse<List<HomepageSectionResponse>>> getSections() {
        return ResponseEntity.ok(ApiResponse.success(homepageService.getActiveSections()));
    }
}