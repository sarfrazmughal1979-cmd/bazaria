package com.platform.payment.api;

import com.platform.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhooks/payment")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/{gateway}")
    public ResponseEntity<Void> handleWebhook(@PathVariable String gateway,
                                              @RequestBody String payload,
                                              @RequestHeader(value = "X-Signature", required = false) String signature) {
        log.info("Webhook received from gateway: {}", gateway);
        paymentService.processWebhook(gateway, payload, signature);
        return ResponseEntity.ok().build();
    }
}
