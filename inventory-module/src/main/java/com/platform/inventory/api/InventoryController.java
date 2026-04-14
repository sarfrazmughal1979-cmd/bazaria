package com.platform.inventory.api;

import com.platform.inventory.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveStockResponse> reserveStock(@RequestBody ReserveStockRequest request) {
        UUID reservationId = inventoryService.reserveStock(
                request.productId(),
                request.variantId(),
                request.quantity()
        );
        return ResponseEntity.ok(new ReserveStockResponse(reservationId));
    }
    @GetMapping("/stock")
    public ResponseEntity<Integer> getAvailableStock(@RequestParam UUID productId,
                                                     @RequestParam(required = false) UUID variantId) {
        int stock = inventoryService.getAvailableStock(productId, variantId);
        return ResponseEntity.ok(stock);
    }
    @PostMapping("/release/{reservationId}")
    public ResponseEntity<Void> releaseReservation(@PathVariable UUID reservationId) {
        inventoryService.releaseReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    public record ReserveStockRequest(UUID productId, UUID variantId, int quantity) {}
    public record ReserveStockResponse(UUID reservationId) {}
}