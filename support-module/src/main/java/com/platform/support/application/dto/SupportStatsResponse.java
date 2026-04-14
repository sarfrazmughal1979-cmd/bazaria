package com.platform.support.application.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportStatsResponse {
    private long openTickets;
    private long pendingDisputes;
    private long activeChats;
    private double averageFirstResponseTimeMinutes;
    private double averageResolutionTimeHours;
    private double customerSatisfactionRate;
    private int ticketsByPriorityLow;
    private int ticketsByPriorityMedium;
    private int ticketsByPriorityHigh;
    private int ticketsByPriorityUrgent;
}