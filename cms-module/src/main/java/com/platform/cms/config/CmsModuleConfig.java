package com.platform.cms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@ComponentScan(basePackageClasses = CmsModuleConfig.class)
@EnableCaching
public class CmsModuleConfig {
}