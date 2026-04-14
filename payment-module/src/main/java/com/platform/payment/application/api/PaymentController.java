package com.platform.payment.application.api;

import com.platform.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/api/internal/payments/refund")
    public ResponseEntity<Void> processRefund(@RequestBody RefundRequest request) {
        paymentService.refund( request.paymentId(), request.amount(), request.reason());
        return ResponseEntity.ok().build();
    }

    public record RefundRequest(UUID paymentId, BigDecimal amount, String reason) {}
}
