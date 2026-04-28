package com.platform.promotion.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = PromotionModuleConfig.class)
@EnableScheduling
public class PromotionModuleConfig {
}
