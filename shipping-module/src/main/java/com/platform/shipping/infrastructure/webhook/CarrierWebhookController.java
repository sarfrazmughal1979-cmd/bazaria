package com.platform.shipping.infrastructure.webhook;

import com.platform.shipping.application.service.CarrierWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhooks/shipping")
@RequiredArgsConstructor
public class CarrierWebhookController {

    private final CarrierWebhookService webhookService;

    @PostMapping("/{carrier}")
    public ResponseEntity<Void> handleWebhook(
            @PathVariable String carrier,
            @RequestBody String payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {
        log.info("Webhook received from carrier: {}", carrier);
        // Verify signature in production
        webhookService.processWebhook(carrier, payload);
        return ResponseEntity.ok().build();
    }
}