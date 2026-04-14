package com.platform.catalog.domain.model;

import com.platform.core.domain.Money;
import com.platform.core.domain.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_vendor", columnList = "vendor_id"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_slug", columnList = "slug", unique = true),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_sku", columnList = "sku", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends TenantAwareEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "base_price")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "price_currency"))
    })
    private Money basePrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "sale_price")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "sale_price_currency"))
    })
    private Money salePrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "cost_price")),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "cost_price_currency"))
    })
    private Money costPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "weight_unit")
    private String weightUnit;

    @Column(name = "is_featured")
    private boolean featured;

    @Column(name = "is_digital")
    private boolean digital;

    @Column(name = "tax_class")
    private String taxClass;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "review_count")
    private int reviewCount;

    @Column(name = "view_count")
    private long viewCount;

    @Column(name = "sold_count")
    private long soldCount;

    @Column(name = "min_order_quantity")
    @Builder.Default
    private int minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductAttribute> attributes = new ArrayList<>();

    @Embedded
    private SEOMetadata seoMetadata;

    // Domain methods
    public Money getEffectivePrice() {
        if (salePrice != null && salePrice.isPositive()) {
            return salePrice;
        }
        return basePrice;
    }

    public BigDecimal getDiscountPercentage() {
        if (salePrice != null && salePrice.isPositive() && basePrice.isPositive()) {
            return basePrice.getAmount().subtract(salePrice.getAmount())
                    .divide(basePrice.getAmount(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void approve() {
        this.status = ProductStatus.ACTIVE;
    }

    public void reject(String reason) {
        this.status = ProductStatus.REJECTED;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void updateRating(BigDecimal newRating, int totalReviews) {
        this.averageRating = newRating;
        this.reviewCount = totalReviews;
    }
}