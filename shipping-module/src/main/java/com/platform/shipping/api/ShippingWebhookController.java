package com.platform.shipping.api;

import com.platform.shipping.application.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhooks/shipping")
@RequiredArgsConstructor
public class ShippingWebhookController {

    private final ShippingService shippingService;

    @PostMapping("/{carrier}")
    public ResponseEntity<Void> handleWebhook(@PathVariable String carrier,
                                              @RequestBody String payload) {
        log.info("Webhook received from carrier: {}", carrier);
        shippingService.processWebhook(carrier, payload);
        return ResponseEntity.ok().build();
    }
}
