package com.platform.settlement.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = SettlementModuleConfig.class)
@EnableScheduling
public class SettlementModuleConfig {
}
