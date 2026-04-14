package com.platform.support.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageRequest {
    @NotBlank
    private String message;
    private boolean internal;   // internal note for agents only
    private List<String> attachments;
}