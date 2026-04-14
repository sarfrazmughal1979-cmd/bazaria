package com.platform.core.cloud.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class StorageProperties {

    private String bucketName;
    private String region;
    private long maxFileSize = 10485760; // 10MB
    private String allowedExtensions = "jpg,jpeg,png,gif,webp,pdf,doc,docx,xls,xlsx";
}