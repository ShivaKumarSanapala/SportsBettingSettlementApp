package com.sportygroup.service;

import com.sportygroup.model.Bet;
import com.sportygroup.model.BetSettlement;
import com.sportygroup.model.BetStatus;
import com.sportygroup.model.EventOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for matching event outcomes to bets and generating settlements
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetMatchingService {
    
    // Simple odds for demonstration (in a real system, this would come from odds service)
    private static final BigDecimal DEFAULT_WINNING_ODDS = new BigDecimal("2.0");
    
    private final BetService betService;
    private final BetSettlementService betSettlementService;
    
    /**
     * Process event outcome and generate bet settlements
     */
    public void processEventOutcome(EventOutcome eventOutcome) {
        log.info("Processing event outcome: {}", eventOutcome);
        
        // Get all pending bets for this event
        List<Bet> pendingBets = betService.getPendingBetsForEvent(eventOutcome.getEventId());
        log.info("Found {} pending bets for event: {}", pendingBets.size(), eventOutcome.getEventId());
        
        if (pendingBets.isEmpty()) {
            log.info("No pending bets found for event: {}", eventOutcome.getEventId());
            return;
        }
        
        List<BetSettlement> settlements = new ArrayList<>();
        
        // Process each bet
        for (Bet bet : pendingBets) {
            BetSettlement settlement = createBetSettlement(bet, eventOutcome);
            settlements.add(settlement);
            
            // Update bet status based on settlement
            BetStatus newStatus = settlement.getSettlementStatus();
            betService.updateBetStatus(bet.getBetId(), newStatus);
        }
        
        // Send settlements to RocketMQ
        for (BetSettlement settlement : settlements) {
            betSettlementService.publishBetSettlement(settlement);
        }
        
        log.info("Processed {} bet settlements for event: {}", settlements.size(), eventOutcome.getEventId());
    }
    
    /**
     * Create bet settlement based on bet and event outcome
     */
    private BetSettlement createBetSettlement(Bet bet, EventOutcome eventOutcome) {
        BetStatus settlementStatus;
        BigDecimal payoutAmount;
        
        // Determine if bet won or lost
        boolean betWon = bet.getEventWinnerId().equals(eventOutcome.getEventWinnerId());
        
        if (betWon) {
            settlementStatus = BetStatus.WON;
            // Calculate payout: bet amount * odds
            payoutAmount = bet.getBetAmount().multiply(DEFAULT_WINNING_ODDS);
            log.debug("Bet {} WON - payout: {}", bet.getBetId(), payoutAmount);
        } else {
            settlementStatus = BetStatus.LOST;
            payoutAmount = BigDecimal.ZERO; // No payout for losing bets
            log.debug("Bet {} LOST - no payout", bet.getBetId());
        }
        
        return new BetSettlement(
            bet.getBetId(),
            bet.getUserId(),
            settlementStatus,
            payoutAmount,
            LocalDateTime.now(),
            eventOutcome.getEventId()
        );
    }
}
