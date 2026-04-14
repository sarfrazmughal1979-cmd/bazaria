package com.platform.support;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = SupportModuleConfig.class)
@EnableScheduling
public class SupportModuleConfig {
}