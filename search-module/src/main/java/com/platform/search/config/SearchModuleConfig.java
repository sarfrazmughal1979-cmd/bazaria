package com.platform.search.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ComponentScan(basePackageClasses = SearchModuleConfig.class)
@EnableElasticsearchRepositories(basePackages = "com.platform.search.domain.repository")
public class SearchModuleConfig {
}