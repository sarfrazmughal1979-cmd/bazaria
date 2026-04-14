package com.platform.promotion.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "flash_sales", indexes = {
        @Index(name = "idx_flash_sale_active", columnList = "is_active"),
        @Index(name = "idx_flash_sale_dates", columnList = "start_time, end_time"),
        @Index(name = "idx_flash_sale_slug", columnList = "slug", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSale extends AuditableEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", unique = true, nullable = false, length = 255)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = false;

    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FlashSaleItem> items = new ArrayList<>();

    // Domain methods
    public boolean isCurrentlyActive() {
        Instant now = Instant.now();
        return isActive && now.isAfter(startTime) && now.isBefore(endTime);
    }

    public boolean hasStarted() {
        return Instant.now().isAfter(startTime);
    }

    public boolean hasEnded() {
        return Instant.now().isAfter(endTime);
    }

    public void activate() {
        if (hasStarted() && !hasEnded()) {
            this.isActive = true;
        } else {
            throw new IllegalStateException("Flash sale cannot be activated outside its time window");
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void addItem(FlashSaleItem item) {
        items.add(item);
        item.setFlashSale(this);
    }

    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }
}