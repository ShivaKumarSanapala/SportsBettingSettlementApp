package com.sportygroup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new bet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBetRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Event ID is required")
    private String eventId;
    
    @NotBlank(message = "Event Market ID is required")
    private String eventMarketId;
    
    @NotBlank(message = "Event Winner ID is required")
    private String eventWinnerId;
    
    @NotNull(message = "Bet amount is required")
    @Positive(message = "Bet amount must be positive")
    private BigDecimal betAmount;
}
