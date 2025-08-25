package com.sportygroup.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a sports bet placed by a user
 */
@Entity
@Table(name = "bets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Bet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long betId;
    
    @NotBlank
    @Column(nullable = false)
    private String userId;
    
    @NotBlank
    @Column(nullable = false)
    private String eventId;
    
    @NotBlank
    @Column(nullable = false)
    private String eventMarketId;
    
    @NotBlank
    @Column(nullable = false)
    private String eventWinnerId;
    
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal betAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BetStatus status = BetStatus.PENDING;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Custom constructor for business logic
    public Bet(String userId, String eventId, String eventMarketId, String eventWinnerId, BigDecimal betAmount) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventMarketId = eventMarketId;
        this.eventWinnerId = eventWinnerId;
        this.betAmount = betAmount;
        this.status = BetStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
