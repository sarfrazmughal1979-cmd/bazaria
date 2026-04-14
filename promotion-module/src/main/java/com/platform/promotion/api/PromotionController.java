package com.platform.promotion.api;

import com.platform.core.exception.ResourceNotFoundException;
import com.platform.promotion.application.service.FlashSaleService;
import com.platform.promotion.application.service.PromotionService;
import com.platform.promotion.domain.model.FlashSale;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;  // ensure this service has the required methods
    private final FlashSaleService flashSaleService;  // ensure this service has the required methods

    @PostMapping("/calculate-discount")
    public ResponseEntity<BigDecimal> calculateDiscount(@RequestBody DiscountRequest request) {
        BigDecimal discount = promotionService.calculateDiscount(
                request.couponCode(),
                request.subtotal(),
                request.customerId()
        );
        return ResponseEntity.ok(discount);
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<Void> applyCoupon(@RequestBody ApplyCouponRequest request) {
        promotionService.applyCoupon(request.couponCode(), request.customerId(), request.orderId());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/flash-sales/{flashSaleId}/info")
    public ResponseEntity<FlashSaleInfo> getFlashSaleInfo(@PathVariable String flashSaleId) {
        FlashSale flashSale = flashSaleService.findById(UUID.fromString(flashSaleId))
                .orElseThrow(() -> new ResourceNotFoundException("FlashSale", "id", flashSaleId));
        return ResponseEntity.ok(new FlashSaleInfo(flashSale.getId().toString(), flashSale.getName()));
    }
@GetMapping("/flash-sales/{flashSaleId}/info-mini")
public ResponseEntity<FlashSaleInfoMini> getFlashSaleInfoMini(@PathVariable String flashSaleId) {
    FlashSale flashSale = flashSaleService.findById(UUID.fromString(flashSaleId)).orElse(null);
    if (flashSale == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(new FlashSaleInfoMini(flashSale.getId().toString(), flashSale.getName()));
}


    public record FlashSaleInfo(String id, String name) {}
	public record FlashSaleInfoMini(String id, String name) {}
    public record DiscountRequest(String couponCode, BigDecimal subtotal, UUID customerId) {}
    public record ApplyCouponRequest(String couponCode, UUID customerId, UUID orderId) {}
}