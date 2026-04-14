package com.platform.payment.application.gateway;

import com.platform.core.exception.BusinessException;
import com.platform.payment.domain.model.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final Map<String, PaymentGatewayAdapter> adapters;

    public PaymentGatewayAdapter getAdapter(PaymentGateway gateway) {
        String beanName = gateway.name().toLowerCase() + "GatewayAdapter";
        PaymentGatewayAdapter adapter = adapters.get(beanName);
        if (adapter == null) {
            throw new BusinessException("UNSUPPORTED_GATEWAY",
                    "Payment gateway not supported: " + gateway);
        }
        return adapter;
    }
}