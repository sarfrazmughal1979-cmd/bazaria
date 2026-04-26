package com.platform.settlement.api;

import com.platform.core.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> status() {
        return ResponseEntity.ok(ApiResponse.success("Settlement service is running"));
    }
}
