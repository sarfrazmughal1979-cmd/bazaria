package com.platform.cms.api;

import com.platform.cms.domain.model.MenuItem;
import com.platform.cms.domain.repository.MenuItemRepository;
import com.platform.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/menus")
@RequiredArgsConstructor
public class PublicMenuController {

    private final MenuItemRepository menuItemRepository;

    @GetMapping("/{location}")
    @Operation(summary = "Get menu by location")
    @Transactional(readOnly = true)   // ← Add this
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenu(@PathVariable String location) {
        List<MenuItem> items = menuItemRepository
            .findByLocationAndParentIsNullAndVisibleTrueAndDeletedFalseOrderBySortOrderAsc(location);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
}