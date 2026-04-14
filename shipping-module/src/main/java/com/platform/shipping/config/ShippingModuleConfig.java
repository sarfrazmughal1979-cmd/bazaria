package com.platform.shipping.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = ShippingModuleConfig.class)
@EnableScheduling
public class ShippingModuleConfig {
}
