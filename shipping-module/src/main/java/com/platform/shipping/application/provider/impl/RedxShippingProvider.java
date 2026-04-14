package com.platform.shipping.application.provider.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("redxShippingProvider")
public class RedxShippingProvider extends PathaoShippingProvider {
    @Override
    public String getProviderName() { return "REDX"; }
}
