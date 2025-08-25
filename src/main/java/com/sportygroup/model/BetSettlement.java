package com.sportygroup.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bet settlement message for RocketMQ
 */
@Data
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BetSettlement {
    
    @EqualsAndHashCode.Include
    private Long betId;
    private String userId;
    private BetStatus settlementStatus;
    private BigDecimal payoutAmount;
    @Builder.Default
    private LocalDateTime settlementTime = LocalDateTime.now();
    private String eventId;

    @JsonCreator
    public BetSettlement(
            @JsonProperty("betId") Long betId,
            @JsonProperty("userId") String userId,
            @JsonProperty("settlementStatus") BetStatus settlementStatus,
            @JsonProperty("payoutAmount") BigDecimal payoutAmount,
            @JsonProperty("settlementTime") LocalDateTime settlementTime,
            @JsonProperty("eventId") String eventId) {
        this.betId = betId;
        this.userId = userId;
        this.settlementStatus = settlementStatus;
        this.payoutAmount = payoutAmount;
        this.settlementTime = settlementTime != null ? settlementTime : LocalDateTime.now();
        this.eventId = eventId;
    }
}
