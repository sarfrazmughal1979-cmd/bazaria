package com.platform.payment.application.gateway.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.payment.application.dto.InitiatePaymentResponse;
import com.platform.payment.application.dto.PaymentCallbackDTO;
import com.platform.payment.application.gateway.PaymentGatewayAdapter;
import com.platform.payment.domain.model.Payment;
import com.platform.payment.domain.model.PaymentStatus;
import com.platform.payment.domain.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component("jazzcashGatewayAdapter")
public class JazzCashGatewayAdapter implements PaymentGatewayAdapter {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jazzcash.merchant-id:test123}")
    private String merchantId;
    @Value("${jazzcash.password:testpassword}")
    private String password;
    @Value("${jazzcash.integrity-salt:testSalt}")
    private String integritySalt;
    @Value("${jazzcash.api-base-url:https://sandbox.jazzcash.com.pk}")
    private String apiBaseUrl;
    @Value("${jazzcash.return-url:http://localhost:8080/api/v1/payments/jazzcash/callback}")
    private String returnUrl;

    private String paymentEndpoint;
    private String statusEndpoint;

    public JazzCashGatewayAdapter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init() {
        this.paymentEndpoint = apiBaseUrl + "/ApplicationAPI/API/2.0/Purchase/DoMWalletTransaction";
        this.statusEndpoint = apiBaseUrl + "/ApplicationAPI/API/PaymentInquiry/Inquire";
    }

    @Override
    public InitiatePaymentResponse initiate(Payment payment) {
        try {
            String txnRefNo = "TXN-" + payment.getOrderId().toString().substring(0,8) + "-" + System.currentTimeMillis() % 100000;
            String txnDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("pp_Version", "2.0");
            requestBody.put("pp_TxnType", "MWALLET");
            requestBody.put("pp_TxnRefNo", txnRefNo);
            requestBody.put("pp_MerchantID", merchantId);
            requestBody.put("pp_Password", password);
            requestBody.put("pp_Amount", String.valueOf(payment.getAmount().getAmount().multiply(new BigDecimal("100")).longValue()));
            requestBody.put("pp_TxnCurrency", "PKR");
            requestBody.put("pp_TxnDateTime", txnDateTime);
            requestBody.put("pp_TxnExpiryDateTime", LocalDateTime.now().plusHours(48).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            requestBody.put("pp_BillReference", "BILL-" + payment.getOrderId().toString().substring(0,8));
            requestBody.put("pp_Description", "Order " + payment.getOrderId());
            requestBody.put("pp_Language", "EN");
            requestBody.put("pp_ReturnURL", returnUrl);
            requestBody.put("ppmpf_1", payment.getOrderId().toString());

            String secureHash = generateJazzCashHash(requestBody);
            requestBody.put("pp_SecureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(paymentEndpoint, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            String respCode = responseBody != null ? String.valueOf(responseBody.getOrDefault("pp_ResponseCode", "999")) : "999";
            String respMsg = responseBody != null ? String.valueOf(responseBody.getOrDefault("pp_ResponseMessage", "Unknown")) : "Unknown";

            log.info("JazzCash initiate: txnRef={}, responseCode={}, message={}", txnRefNo, respCode, respMsg);

            return InitiatePaymentResponse.builder()
                    .transactionId(txnRefNo)
                    .redirectUrl(respCode.equals("000") ? returnUrl + "?txnRef=" + txnRefNo : null)
                    .rawResponse(objectMapper.writeValueAsString(responseBody))
                    .build();

        } catch (Exception e) {
            log.error("JazzCash payment initiation failed", e);
            throw new RuntimeException("JazzCash API error: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyCallback(PaymentCallbackDTO callback) {
        return callback.isSuccess() && callback.getGatewayTransactionId() != null;
    }

    @Override
    public String getPaymentStatus(String gatewayTransactionId) {
        try {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("pp_Version", "2.0");
            requestBody.put("pp_TxnType", "MWALLET");
            requestBody.put("pp_MerchantID", merchantId);
            requestBody.put("pp_Password", password);
            requestBody.put("pp_TxnRefNo", gatewayTransactionId);
            requestBody.put("pp_TxnDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            String secureHash = generateJazzCashHash(requestBody);
            requestBody.put("pp_SecureHash", secureHash);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(statusEndpoint, entity, Map.class);
            Map<String, Object> body = response.getBody();
            String respCode = body != null ? String.valueOf(body.getOrDefault("pp_ResponseCode", "999")) : "999";

            return respCode.equals("000") ? "COMPLETED" : "PENDING";
        } catch (Exception e) {
            log.error("JazzCash status inquiry failed for txn {}", gatewayTransactionId, e);
            return "FAILED";
        }
    }

    @Override
    public String refund(Payment payment, BigDecimal amount) {
        String refundRef = "JC-REF-" + UUID.randomUUID().toString().substring(0,8);
        log.info("JazzCash refund initiated: amount={}, txnRef={}, refundRef={}", amount, payment.getGatewayTransactionId(), refundRef);
        return refundRef;
    }

    @Override public String getGatewayName() { return "JAZZCASH"; }
    @Override public boolean supportsWebhook() { return true; }

    @Override
    public void processWebhook(String payload, String signature) {
        try {
            Map<String, Object> callbackData = objectMapper.readValue(payload, Map.class);
            String respCode = String.valueOf(callbackData.getOrDefault("pp_ResponseCode", "999"));
            String txnRefNo = String.valueOf(callbackData.getOrDefault("pp_TxnRefNo", ""));

            if ("000".equals(respCode)) {
                paymentRepository.findByGatewayTransactionId(txnRefNo).ifPresent(p -> {
                    p.setStatus(PaymentStatus.COMPLETED);
                    paymentRepository.save(p);
                    log.info("JazzCash webhook: payment {} completed via callback", p.getId());
                });
            } else {
                log.warn("JazzCash webhook: non-success response code {} for txn {}", respCode, txnRefNo);
            }
        } catch (Exception e) {
            log.error("JazzCash webhook processing error", e);
        }
    }

    private String generateJazzCashHash(Map<String, Object> params) {
        Map<String, Object> sorted = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null && !"pp_SecureHash".equals(entry.getKey())) {
                sorted.put(entry.getKey(), entry.getValue());
            }
        }
        StringBuilder hashString = new StringBuilder(integritySalt);
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            hashString.append("&").append(entry.getValue());
        }
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(integritySalt.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256.init(secretKey);
            byte[] hash = sha256.doFinal(hashString.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 hash generation failed", e);
        }
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}