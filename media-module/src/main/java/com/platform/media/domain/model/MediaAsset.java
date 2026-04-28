package com.platform.media.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media_assets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MediaAsset extends AuditableEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size_in_bytes")
    private long sizeInBytes;

    @Column(name = "bucket_key", nullable = false, unique = true)
    private String bucketKey;       // S3 object key

    @Column(name = "url", length = 2000)
    private String url;             // public or presigned URL (cached)

    @Column(name = "width")
    private Integer width;          // for images/videos

    @Column(name = "height")
    private Integer height;

    @Column(name = "entity_type", length = 50)
    private String entityType;      // PRODUCT, BANNER, REVIEW, etc.

    @Column(name = "entity_id")
    private java.util.UUID entityId;

    @Column(name = "tags", length = 500)
    private String tags;            // comma-separated
}