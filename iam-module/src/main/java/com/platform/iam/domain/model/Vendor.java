package com.platform.iam.domain.model;

import com.platform.core.domain.Address;
import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vendors", indexes = {
    @Index(name = "idx_vendor_shop_name", columnList = "shop_name"),
    @Index(name = "idx_vendor_slug", columnList = "slug", unique = true),
    @Index(name = "idx_vendor_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "shop_description", columnDefinition = "TEXT")
    private String shopDescription;

    @Column(name = "shop_logo_url")
    private String shopLogoUrl;

    @Column(name = "shop_banner_url")
    private String shopBannerUrl;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "business_address_line1", insertable=false, updatable=false)),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "business_address_line2", insertable=false, updatable=false)),
        @AttributeOverride(name = "city", column = @Column(name = "business_city", insertable=false, updatable=false)),
        @AttributeOverride(name = "state", column = @Column(name = "business_state", insertable=false, updatable=false)),
        @AttributeOverride(name = "postalCode", column = @Column(name = "business_postal_code", insertable=false, updatable=false)),
        @AttributeOverride(name = "country", column = @Column(name = "business_country", insertable=false, updatable=false)),
//        @AttributeOverride(name = "latitude", column = @Column(name = "business_latitude", insertable=false, updatable=false)),
        @AttributeOverride(name = "longitude", column = @Column(name = "business_longitude", insertable=false, updatable=false))
    })
    private Address businessAddress;

    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_routing_number")
    private String bankRoutingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VendorStatus status;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_products")
    private int totalProducts;

    @Column(name = "total_orders")
    private int totalOrders;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public void approve(UUID adminId) {
        this.status = VendorStatus.ACTIVE;
        this.approvedAt = Instant.now();
        this.approvedBy = adminId;
    }

    public void reject(String reason) {
        this.status = VendorStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public void suspend(String reason) {
        this.status = VendorStatus.SUSPENDED;
        this.rejectionReason = reason;
    }

    public boolean isActive() {
        return this.status==VendorStatus.ACTIVE;
    }
}