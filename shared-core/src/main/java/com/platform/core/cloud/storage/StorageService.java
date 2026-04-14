package com.platform.core.cloud.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    String upload(MultipartFile file, String folder);

    List<String> uploadMultiple(List<MultipartFile> files, String folder);

    void delete(String fileUrl);

    String getPresignedUrl(String key, int expirationMinutes);
}