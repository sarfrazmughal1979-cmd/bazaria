package com.platform.order.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@ComponentScan(basePackageClasses = OrderModuleConfig.class)
@EnableCaching
public class OrderModuleConfig {
}
