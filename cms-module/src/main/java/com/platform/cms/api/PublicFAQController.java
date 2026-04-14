package com.platform.cms.api;

import com.platform.cms.application.dto.FAQResponse;
import com.platform.cms.application.service.FAQService;
import com.platform.cms.domain.model.FAQCategory;
import com.platform.core.dto.ApiResponse;
import com.platform.core.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/faqs")
@RequiredArgsConstructor
public class PublicFAQController {

    private final FAQService faqService;

    @GetMapping("/categories")
    @Operation(summary = "Get all FAQ categories")
    public ResponseEntity<ApiResponse<List<FAQCategory>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(faqService.getVisibleCategories()));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get FAQs by category")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> getFAQsByCategory(
            @PathVariable UUID categoryId) {
        return ResponseEntity.ok(ApiResponse.success(faqService.getFAQsByCategory(categoryId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search FAQs")
    public ResponseEntity<ApiResponse<PagedResponse<FAQResponse>>> searchFAQs(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(faqService.searchFAQs(keyword, pageable)));
    }

    @PostMapping("/{faqId}/helpful")
    @Operation(summary = "Mark FAQ as helpful")
    public ResponseEntity<ApiResponse<Void>> markHelpful(@PathVariable UUID faqId) {
        faqService.markHelpful(faqId);
        return ResponseEntity.ok(ApiResponse.success("Thank you for your feedback"));
    }
}