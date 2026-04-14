package com.platform.payment.application.gateway;

import com.platform.payment.application.dto.InitiatePaymentResponse;
import com.platform.payment.application.dto.PaymentCallbackDTO;
import com.platform.payment.domain.model.Payment;

import java.math.BigDecimal;

public interface PaymentGatewayAdapter {

    /**
     * Initiate a payment with the gateway.
     * @param payment the payment entity (contains amount, currency, orderId, etc.)
     * @return response containing transaction ID and redirect URL (if any)
     */
    InitiatePaymentResponse initiate(Payment payment);

    /**
     * Verify and process a callback/notification from the gateway.
     * @param callback the callback payload
     * @return true if the callback is valid and payment was successful
     */
    boolean verifyCallback(PaymentCallbackDTO callback);

    /**
     * Get the status of a payment from the gateway.
     * @param gatewayTransactionId the transaction ID from the gateway
     * @return current status (e.g., "SUCCESS", "FAILED", "PENDING")
     */
    String getPaymentStatus(String gatewayTransactionId);

    /**
     * Refund a previously captured payment.
     * @param payment the payment to refund
     * @param amount amount to refund (may be partial)
     * @return refund transaction ID
     */
    String refund(Payment payment, BigDecimal amount);

    /**
     * Get the name of this gateway (e.g., "STRIPE", "BKASH").
     */
    String getGatewayName();

    /**
     * Whether this gateway supports webhook callbacks.
     */
    boolean supportsWebhook();

    /**
     * Parse and process a webhook payload from the gateway.
     * @param payload raw webhook body
     * @param signature optional signature header for verification
     */
    void processWebhook(String payload, String signature);
}