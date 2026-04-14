package com.platform.payment.application.gateway.impl;

import com.platform.payment.application.dto.InitiatePaymentResponse;
import com.platform.payment.application.dto.PaymentCallbackDTO;
import com.platform.payment.application.gateway.PaymentGatewayAdapter;
import com.platform.payment.domain.model.Payment;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component("stripeGatewayAdapter")
public class StripeGatewayAdapter implements PaymentGatewayAdapter {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Override
    public InitiatePaymentResponse initiate(Payment payment) {
        Stripe.apiKey = stripeSecretKey;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount().getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency(payment.getAmount().getCurrencyCode().toLowerCase())
                    .setDescription("Order " + payment.getOrderId())
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            return InitiatePaymentResponse.builder()
                    .transactionId(intent.getId())
                    .redirectUrl(intent.getNextAction() != null ? intent.getNextAction().getRedirectToUrl().getUrl() : null)
                    .rawResponse(intent.toJson())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyCallback(PaymentCallbackDTO callback) { return callback.isSuccess(); }
    @Override
    public String getPaymentStatus(String gatewayTransactionId) { return "COMPLETED"; }
    @Override
    public String refund(Payment payment, BigDecimal amount) { return UUID.randomUUID().toString(); }
    @Override
    public String getGatewayName() { return "STRIPE"; }
    @Override
    public boolean supportsWebhook() { return true; }
    @Override
    public void processWebhook(String payload, String signature) {}
}
