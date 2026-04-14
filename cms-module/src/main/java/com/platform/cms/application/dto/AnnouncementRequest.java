package com.platform.cms.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotBlank
    private String type;

    private String linkUrl;

    private String linkText;

    private String icon;

    private Boolean active;

    private Boolean dismissible;

    private Instant startDate;

    private Instant endDate;

    private String targetPages;

    private Integer priority;
}