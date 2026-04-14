package com.platform.catalog.domain.model;
import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "sort_order")
    @Builder.Default
    private int sortOrder = 0;

    @Column(name = "is_primary")
    @Builder.Default
    private boolean primary = false;
}