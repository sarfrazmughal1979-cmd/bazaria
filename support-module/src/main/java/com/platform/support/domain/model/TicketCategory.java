package com.platform.support.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "support_ticket_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCategory extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "auto_assign_role")
    private String autoAssignRole;  // VENDOR_SUPPORT, CUSTOMER_SUPPORT, TECHNICAL
}