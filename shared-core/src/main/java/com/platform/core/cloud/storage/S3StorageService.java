package com.platform.core.cloud.storage;

import com.platform.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties properties;

    @Override
    public String upload(MultipartFile file, String folder) {
        validateFile(file);

        String key = generateKey(folder, file.getOriginalFilename());

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    properties.getBucketName(), properties.getRegion(), key);

            log.info("File uploaded successfully: {}", url);
            return url;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new BusinessException("FILE_UPLOAD_ERROR", "Failed to upload file");
        }
    }

    @Override
    public List<String> uploadMultiple(List<MultipartFile> files, String folder) {
        return files.stream()
                .map(file -> upload(file, folder))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
            log.info("File deleted: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", fileUrl, e);
        }
    }

    @Override
    public String getPresignedUrl(String key, int expirationMinutes) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(b -> b.bucket(properties.getBucketName()).key(key))
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "File is empty");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new BusinessException("FILE_TOO_LARGE",
                    "File size exceeds maximum allowed size of "
                            + (properties.getMaxFileSize() / 1024 / 1024) + "MB");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        List<String> allowed = Arrays.asList(properties.getAllowedExtensions().split(","));
        if (!allowed.contains(extension.toLowerCase())) {
            throw new BusinessException("INVALID_FILE_TYPE",
                    "File type '" + extension + "' is not allowed");
        }
    }

    private String generateKey(String folder, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return folder + "/" + UUID.randomUUID() + "." + extension;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String extractKeyFromUrl(String fileUrl) {
        int index = fileUrl.indexOf(".com/");
        if (index >= 0) {
            return fileUrl.substring(index + 5);
        }
        return fileUrl;
    }
}