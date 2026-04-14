package com.platform.cms.application.dto;

import com.platform.cms.domain.model.FAQCategory;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQResponse {
    private UUID id;
    private UUID categoryId;
    private FAQCategory category;
    private String categoryName;
    private String question;
    private String answer;
    private int sortOrder;
    private boolean visible;
    private int helpfulCount;
    private int notHelpfulCount;
    private Instant createdAt;
    private Instant updatedAt;
}