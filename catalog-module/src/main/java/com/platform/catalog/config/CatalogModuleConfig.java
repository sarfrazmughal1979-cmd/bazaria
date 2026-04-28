package com.platform.catalog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@ComponentScan(basePackageClasses = CatalogModuleConfig.class)
@EnableCaching
public class CatalogModuleConfig {
}
