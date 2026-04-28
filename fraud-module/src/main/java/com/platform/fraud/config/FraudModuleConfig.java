package com.platform.fraud.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = FraudModuleConfig.class)
public class FraudModuleConfig {
}