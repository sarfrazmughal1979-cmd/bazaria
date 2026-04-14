package com.platform.analytics.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRangeRequest {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private String groupBy;  // DAY, WEEK, MONTH
}