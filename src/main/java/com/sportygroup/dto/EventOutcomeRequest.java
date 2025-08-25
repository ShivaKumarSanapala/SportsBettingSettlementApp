package com.sportygroup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Data Transfer Object for publishing event outcomes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventOutcomeRequest {
    
    @NotBlank(message = "Event ID is required")
    private String eventId;
    
    @NotBlank(message = "Event name is required")
    private String eventName;
    
    @NotBlank(message = "Event winner ID is required")
    private String eventWinnerId;
}
