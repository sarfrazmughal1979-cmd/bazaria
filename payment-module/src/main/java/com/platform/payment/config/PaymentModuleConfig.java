package com.platform.payment.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@ComponentScan(basePackageClasses = PaymentModuleConfig.class)
@EnableCaching
public class PaymentModuleConfig {
}
