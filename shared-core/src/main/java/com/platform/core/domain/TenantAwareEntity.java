package com.platform.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class TenantAwareEntity extends AuditableEntity {

    @Column(name = "vendor_id")
    private UUID vendorId;
}