package com.platform.cms.application.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String linkUrl;
    private String linkText;
    private String icon;
    private boolean active;
    private boolean dismissible;
    private Instant startDate;
    private Instant endDate;
    private String targetPages;
    private int priority;
    private Instant createdAt;
}