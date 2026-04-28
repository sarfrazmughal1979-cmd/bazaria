package com.platform.search.api;

import com.platform.core.dto.ApiResponse;
import com.platform.search.application.service.SearchService;
import com.platform.search.domain.model.ProductDocument;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Full-text product search with filters")
    public ResponseEntity<ApiResponse<Page<ProductDocument>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductDocument> results = searchService.search(
                keyword, categoryId, minPrice, maxPrice, sortBy, sortDir, page, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products")
    public ResponseEntity<ApiResponse<Page<ProductDocument>>> featured(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(searchService.findFeatured(page, size)));
    }

    @GetMapping("/autocomplete")
    @Operation(summary = "Search suggestions")
    public ResponseEntity<ApiResponse<List<String>>> autocomplete(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success(searchService.autoComplete(prefix, size)));
    }

    @PostMapping("/admin/reindex")
    @Operation(summary = "Reindex all products (trigger manually)")
    public ResponseEntity<ApiResponse<Void>> reindex() {
        // Placeholder for full reindex
        return ResponseEntity.ok(ApiResponse.success("Reindexing started"));
    }
}