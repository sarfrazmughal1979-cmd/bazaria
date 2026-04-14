package com.platform.catalog.domain.model;

public enum ProductStatus {
    DRAFT,              // vendor is still editing
    PENDING_APPROVAL,   // submitted for admin review
    ACTIVE,             // approved and visible
    REJECTED,           // rejected by admin
    OUT_OF_STOCK,       // temporarily unavailable (auto from inventory)
    DISCONTINUED,       // permanently removed
    DELETED
}