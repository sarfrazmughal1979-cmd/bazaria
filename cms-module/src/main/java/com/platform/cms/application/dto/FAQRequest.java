package com.platform.cms.application.dto;

import com.platform.cms.domain.model.FAQCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQRequest {

    private FAQCategory category;

    private UUID categoryId;

    @NotBlank
    private String question;

    @NotBlank
    private String answer;

    private Integer sortOrder;

    private Boolean visible;
}