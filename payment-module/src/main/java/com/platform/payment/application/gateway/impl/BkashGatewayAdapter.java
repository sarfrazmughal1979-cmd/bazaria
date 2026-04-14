package com.platform.payment.application.gateway.impl;

import com.platform.payment.application.dto.InitiatePaymentResponse;
import com.platform.payment.application.dto.PaymentCallbackDTO;
import com.platform.payment.application.gateway.PaymentGatewayAdapter;
import com.platform.payment.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component("bkashGatewayAdapter")
public class BkashGatewayAdapter implements PaymentGatewayAdapter {

    @Value("${bkash.app-key:test}")
    private String appKey;

    @Override
    public InitiatePaymentResponse initiate(Payment payment) {
        return InitiatePaymentResponse.builder()
                .transactionId("bkash_" + System.currentTimeMillis())
                .redirectUrl("https://sandbox.bkash.com/pay")
                .build();
    }

    @Override
    public boolean verifyCallback(PaymentCallbackDTO callback) { return callback.isSuccess(); }
    @Override
    public String getPaymentStatus(String gatewayTransactionId) { return "COMPLETED"; }
    @Override
    public String refund(Payment payment, BigDecimal amount) { return "refund_" + System.currentTimeMillis(); }
    @Override
    public String getGatewayName() { return "BKASH"; }
    @Override
    public boolean supportsWebhook() { return true; }
    @Override
    public void processWebhook(String payload, String signature) { log.info("bKash webhook: {}", payload); }
}
