package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cms_menu_items", indexes = {
    @Index(name = "idx_menu_parent", columnList = "parent_id"),
    @Index(name = "idx_menu_location", columnList = "location")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem extends AuditableEntity {

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "url")
    private String url;

    @Column(name = "link_type")
    private String linkType;  // PAGE, CATEGORY, PRODUCT, EXTERNAL, VENDOR

    @Column(name = "link_value")
    private String linkValue;

    @Column(name = "icon")
    private String icon;

    @Column(name = "location", nullable = false)
    private String location;  // HEADER_MAIN, HEADER_TOP, FOOTER_1, FOOTER_2, FOOTER_3, FOOTER_4, MOBILE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MenuItem parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<MenuItem> children = new ArrayList<>();

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_visible")
    private boolean visible;

    @Column(name = "open_in_new_tab")
    private boolean openInNewTab;

    @Column(name = "requires_login")
    private boolean requiresLogin;

    @Column(name = "roles_allowed")
    private String rolesAllowed;  // comma-separated role names
}