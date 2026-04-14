package com.platform.cms.domain.model;

import com.platform.core.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cms_faqs", indexes = {
    @Index(name = "idx_faq_category", columnList = "category_id"),
    @Index(name = "idx_faq_question", columnList = "question")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQ extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FAQCategory category;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_visible")
    private boolean visible;

    @Column(name = "helpful_count")
    private int helpfulCount;

    @Column(name = "not_helpful_count")
    private int notHelpfulCount;
}