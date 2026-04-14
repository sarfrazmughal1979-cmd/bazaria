package com.platform.support.application.dto;

import com.platform.support.domain.model.TicketCategory;
import com.platform.support.domain.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    private UUID vendorId;      // optional
    private UUID orderId;       // optional
    private UUID categoryId;

    private TicketCategory category;

    @NotBlank
    private String subject;

    @NotBlank
    private String description;

    private TicketPriority priority;    // LOW, MEDIUM, HIGH, URGENT (default MEDIUM)
    private List<String> attachments;
}