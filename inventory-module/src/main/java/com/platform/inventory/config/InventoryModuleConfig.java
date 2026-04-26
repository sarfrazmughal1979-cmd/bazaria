package com.platform.inventory.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = InventoryModuleConfig.class)
@EnableScheduling
public class InventoryModuleConfig {
}
