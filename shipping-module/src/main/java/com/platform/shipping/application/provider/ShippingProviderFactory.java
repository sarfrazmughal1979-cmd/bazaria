package com.platform.shipping.application.provider;

import com.platform.core.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ShippingProviderFactory {

    private final Map<String, ShippingProviderAdapter> providerMap;

    public ShippingProviderFactory(List<ShippingProviderAdapter> adapters) {
        this.providerMap = adapters.stream()
                .collect(Collectors.toMap(
                        ShippingProviderAdapter::getProviderName,
                        Function.identity()
                ));
    }

    public ShippingProviderAdapter getAdapter(String providerName) {
        ShippingProviderAdapter adapter = providerMap.get(providerName);
        if (adapter == null) {
            throw new BusinessException("UNKNOWN_SHIPPING_PROVIDER",
                    "No adapter found for provider: " + providerName);
        }
        return adapter;
    }
}