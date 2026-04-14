package com.platform.iam.domain.model;

import com.platform.core.domain.AuditableEntity;
import com.platform.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends AuditableEntity {

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "module", length = 50)
    private String module;
}