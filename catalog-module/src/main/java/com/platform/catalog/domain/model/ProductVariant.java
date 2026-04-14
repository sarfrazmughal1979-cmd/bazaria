package com.platform.catalog.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.BaseEntity;
import com.platform.core.domain.Money;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_variant_product", columnList = "product_id"),
        @Index(name = "idx_variant_sku", columnList = "sku", unique = true),
        @Index(name = "idx_variant_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku", unique = true, nullable = false, length = 100)
    private String sku;

    @Column(name = "name", length = 255)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "price_currency"))
    })
    private Money price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;  // optional variant-specific weight (overrides product weight)

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @ElementCollection
    @CollectionTable(name = "variant_attributes",
            joinColumns = @JoinColumn(name = "variant_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    // Domain methods
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}