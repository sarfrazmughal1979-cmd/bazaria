package com.platform.analytics.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = AnalyticsModuleConfig.class)
@EnableScheduling
public class AnalyticsModuleConfig {
}