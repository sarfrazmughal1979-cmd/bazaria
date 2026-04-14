package com.platform.cms.api;

import com.platform.cms.application.dto.AnnouncementResponse;
import com.platform.cms.application.service.AnnouncementService;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/announcements")
@RequiredArgsConstructor
public class PublicAnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Get all active announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getActiveAnnouncements() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getActiveAnnouncements()));
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Get announcements for specific page")
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> getAnnouncementsForPage(
            @PathVariable String page) {
        return ResponseEntity.ok(ApiResponse.success(
            announcementService.getActiveAnnouncementsForPage(page)));
    }
}